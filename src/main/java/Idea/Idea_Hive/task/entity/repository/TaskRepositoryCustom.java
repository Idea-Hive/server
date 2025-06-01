package Idea.Idea_Hive.task.entity.repository;

import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.TaskType;

import java.util.List;

/**
 * QueryDsl Repository Interface
 */
public interface TaskRepositoryCustom {
    List<Task> findTasksByProjectId(Long projectId);
    List<Task> findTasksByProjectIdAndTaskType(Long projectId, TaskType taskType);
}
