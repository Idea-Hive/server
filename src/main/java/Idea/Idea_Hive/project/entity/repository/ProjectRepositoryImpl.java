package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.hashtag.entity.QHashtag;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.QProject;
import Idea.Idea_Hive.project.entity.QProjectHashtag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ProjectRepositoryImpl implements ProjectRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Project> searchByTitleAndHashtag(String keyword, String recruitType, Long hashtagId) {
        QProject project = QProject.project;
        QProjectHashtag projectHashtag = QProjectHashtag.projectHashtag;
        QHashtag hashtag = QHashtag.hashtag;

        BooleanBuilder builder = new BooleanBuilder();

        //키워드 검색 조건
        if (StringUtils.hasText(keyword)) {
            builder.and(project.title.contains(keyword));
        }

        //모집 상태 조건
        if (StringUtils.hasText(recruitType)) {
            builder.and(createRecruiteCondition(recruitType, project));
        }

        if (hashtagId != null) {
            return queryFactory
                    .selectFrom(project)
                    .join(projectHashtag).on(projectHashtag.project.eq(project))
                    .where(
                            builder,
                            projectHashtag.hashtag.id.eq(hashtagId)
                    )
                    .orderBy(project.searchDate.desc())
                    .fetch();
        }
        return queryFactory
                .selectFrom(project)
                .where(builder)
                .orderBy(project.searchDate.desc())
                .fetch();
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
}
