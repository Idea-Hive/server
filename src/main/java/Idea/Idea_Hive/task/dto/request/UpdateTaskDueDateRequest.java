package Idea.Idea_Hive.task.dto.request;

import java.time.LocalDateTime;

public record UpdateTaskDueDateRequest(
        Long taskId,
        LocalDateTime dueDate
) {
}
