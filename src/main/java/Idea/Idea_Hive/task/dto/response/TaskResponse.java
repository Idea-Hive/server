package Idea.Idea_Hive.task.dto.response;

import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.TaskType;

import java.util.Date;

public record TaskResponse(
        Long id,
        Boolean isRequired,
        Boolean isSubmitted,
        String title,
        TaskType taskType,
        String filePath,
        String pic,
        Date dueDate,
        Date uploadDate
) {

    private TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getIsRequired(),
                task.getIsSubmitted(),
                task.getTitle(),
                task.getTaskType(),
                task.getFilePath(),
                task.getPic(),
                task.getDueDate(),
                task.getUploadDate()
        );
    }
}
