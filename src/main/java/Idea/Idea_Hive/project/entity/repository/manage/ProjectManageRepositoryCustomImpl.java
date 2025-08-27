package Idea.Idea_Hive.project.entity.repository.manage;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.QMember;
import Idea.Idea_Hive.project.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<Member> findMemberByProjectId(Long projectId) {
        QProject project = QProject.project;
        QMember member = QMember.member;
        QProjectMember projectMember = QProjectMember.projectMember;

        return jpaQueryFactory
                .select(member)
                .from(member)
                .join(projectMember).on(member.id.eq(projectMember.member.id))
                .join(project).on(projectMember.project.id.eq(project.id))
                .where(project.id.eq(projectId))
                .fetch();
    }

    @Override
    public Page<Project> findProjectByMemberIdWithPage(Long memberId, Pageable pageable) {
        QProjectMember projectMember = QProjectMember.projectMember;
        QProject project = QProject.project;

        List<Project> projects =  jpaQueryFactory
                .select(project)
                .from(project)
                .join(project.projectMembers, projectMember)
                .where(
                        projectMember.member.id.eq(memberId),
                        project.status.ne(ProjectStatus.RECRUITING) // RECRUITING이 아닌 프로젝트만
                                .or(project.status.eq(ProjectStatus.RECRUITING)
                                        .and(project.isNew.eq(false)))
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
                        projectMember.member.id.eq(memberId),
                        project.status.ne(ProjectStatus.RECRUITING) // RECRUITING이 아닌 프로젝트만
                                .or(project.status.eq(ProjectStatus.RECRUITING)
                                        .and(project.isNew.eq(false)))
                )
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(projects, pageable, total);
    }

    @Override
    public List<Project> findProjectByMemberId(Long memberId) {
        QProjectMember projectMember = QProjectMember.projectMember;
        QProject project = QProject.project;

        return jpaQueryFactory
                .select(project)
                .from(project)
                .join(project.projectMembers, projectMember)
                .where(projectMember.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public List<Project> findLikeProjectByMemberId(Long memberId){
        QProjectMember projectMember = QProjectMember.projectMember;
        QProject project = QProject.project;

        return jpaQueryFactory
                .select(project)
                .from(project)
                .join(project.projectMembers, projectMember)
                .where(
                        projectMember.member.id.eq(memberId),
                        projectMember.isLike.isTrue())
                .fetch();
    }
}
