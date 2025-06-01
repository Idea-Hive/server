package Idea.Idea_Hive.task.dto.request;

import Idea.Idea_Hive.task.entity.TaskType;

public record ProjectTaskListRequest(
        Long projectId,
        TaskType taskType
) {
}
