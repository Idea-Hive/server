package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.hashtag.entity.QHashtag;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.QProject;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ProjectRepositoryImpl implements ProjectRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Project> searchByKeyword(String keyword, String recruitType, String sortType, Pageable pageable) {
        QProject project = QProject.project;
        QHashtag hashtag = QHashtag.hashtag;

        BooleanBuilder builder = new BooleanBuilder();

        //모집 상태 조건
        if (StringUtils.hasText(recruitType)) {
            builder.and(createRecruiteCondition(recruitType, project));
        }

        List<Project> projects = queryFactory
                .selectFrom(project)
                .distinct()
                .leftJoin(project.hashtags, hashtag)
                .where(
                        builder,
                        keyword != null ?
                                project.title.contains(keyword)
                                        .or(project.hashtags.any().name.contains(keyword))
                                : null
                )
                .orderBy(createOrderSpecifier(sortType, project))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(project.countDistinct())
                .from(project)
                .leftJoin(project.hashtags, hashtag)
                .where(
                        builder,
                        keyword != null ?
                                project.title.contains(keyword)
                                        .or(project.hashtags.any().name.contains(keyword))
                                : null
                )
                .fetchOne();

        return new PageImpl<>(projects, pageable, total);

    }

    // 모집 상태에 따른 조건 생성
    private BooleanExpression createRecruiteCondition(String recruitType, QProject project) {
        if (recruitType == null) return null;

        switch (recruitType) {
            case "NEW":
                return project.status.eq(ProjectStatus.RECRUITING)
                        .and(project.isNew.isTrue());
            case "ADDITIONAL":
                return project.status.eq(ProjectStatus.RECRUITING)
                        .and(project.isNew.isFalse());
            case "ALL":
                return project.status.eq(ProjectStatus.RECRUITING);
            default:
                return null;
        }
    }

    // 최신순, 마감임박순 정렬
    private OrderSpecifier<?>[] createOrderSpecifier(String sortType, QProject project) {
        if ("RECENT".equals(sortType)) {
            return new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, project.searchDate),
                    new OrderSpecifier<>(Order.DESC, project.createdDate)
            };
        } else {
            return new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, project.searchDate),
                    new OrderSpecifier<>(Order.DESC, project.expirationDate)
            };
        }
    }
}
