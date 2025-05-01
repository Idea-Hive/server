package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.hashtag.entity.QHashtag;
import Idea.Idea_Hive.project.entity.QProjectSkillStack;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.QProject;
import Idea.Idea_Hive.skillstack.entity.QSkillStack;
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
    public List<Project> searchByKeyword(String keyword, String recruitType) {
        QProject project = QProject.project;
        QHashtag hashtag = QHashtag.hashtag;

        BooleanBuilder builder = new BooleanBuilder();

        //모집 상태 조건
        if (StringUtils.hasText(recruitType)) {
            builder.and(createRecruiteCondition(recruitType, project));
        }

        return queryFactory
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
