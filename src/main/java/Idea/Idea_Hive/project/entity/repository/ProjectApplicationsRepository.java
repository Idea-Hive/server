package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.project.entity.ProjectApplications;
import Idea.Idea_Hive.project.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ProjectApplicationsRepository extends JpaRepository<ProjectApplications, Long> {
    Optional<ProjectApplications> findById(Long id);

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);

    Optional<ProjectApplications> findTopByProjectIdAndMemberIdOrderByApplicationDateDesc(Long projectId, Long memberId);

    void delete(ProjectApplications projectApplications);
}
