package Idea.Idea_Hive.project.entity.repository.manage;

import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.QProject;
import Idea.Idea_Hive.project.entity.QProjectMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ProjectManageRepositoryCustomImpl implements ProjectManageRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Project> findProjectByMemberIdAndStatusWithPage(Long memberId, ProjectStatus status, Pageable pageable) {

        QProjectMember projectMember = QProjectMember.projectMember;
        QProject project = QProject.project;

        List<Project> projects =  jpaQueryFactory
                .select(project)
                .from(project)
                .join(project.projectMembers, projectMember)
                .where(
                        project.status.eq(status),
                        projectMember.member.id.eq(memberId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(project.createdDate.desc())
                .fetch();

        // 전체 개수 쿼리
        long total = Optional.ofNullable(jpaQueryFactory
                .select(project.count())
                .from(project)
                .join(project.projectMembers, projectMember)
                .where(
                        project.status.eq(status),
                        projectMember.member.id.eq(memberId)
                )
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(projects, pageable, total);

    }
}
