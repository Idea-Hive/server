package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.project.dto.response.ProjectTempSavedResponse;
import Idea.Idea_Hive.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {
    List<Project> findByProjectMembers_MemberIdAndIsSaveFalseOrderByCreatedDateDesc(Long memberId);
}
