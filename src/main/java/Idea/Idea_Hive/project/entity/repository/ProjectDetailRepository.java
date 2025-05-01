package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.project.entity.ProjectDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDetailRepository extends JpaRepository<ProjectDetail, Long> {
}
