package Idea.Idea_Hive.project.entity.repository.manage;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectManageRepositoryCustom {

    Page<Project> findProjectByMemberIdAndStatusWithPage(Long memberId, ProjectStatus status, Pageable pageable);
    List<Member> findMemberByProjectId(Long projectId);
    Page<Project> findProjectByMemberIdWithPage(Long memberId, Pageable pageable);
}
