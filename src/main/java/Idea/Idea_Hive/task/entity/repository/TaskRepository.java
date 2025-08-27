package Idea.Idea_Hive.task.entity.repository;

import Idea.Idea_Hive.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
}
