package Idea.Idea_Hive.task.dto.request;

import Idea.Idea_Hive.task.entity.TaskType;

public record CreateTaskRequest(
        Boolean isRequired,
        String title,
        TaskType taskType
) {
}
