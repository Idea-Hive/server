package Idea.Idea_Hive.task.entity.repository;

import Idea.Idea_Hive.task.entity.Task;

import java.util.List;

/**
 * QueryDsl Repository Interface
 */
public interface TaskRepositoryCustom {
    List<Task> findTasksByProjectId(Long projectId);
}
