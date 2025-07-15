package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.project.entity.ProjectMember;
import Idea.Idea_Hive.project.entity.ProjectMemberId;
import Idea.Idea_Hive.project.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Long> {
    Optional<ProjectMember> findByProjectIdAndMemberId(Long projectId, Long memberId);

    Optional<ProjectMember> findByProjectIdAndRole(Long projectId, Role role);
}
