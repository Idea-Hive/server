package Idea.Idea_Hive.task.dto.request;

import java.util.Date;

public record UpdateTaskDueDateRequest(
        Long taskId,
        Date dueDate
) {
}
