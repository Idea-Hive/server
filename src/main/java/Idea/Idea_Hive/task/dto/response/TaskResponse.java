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
        String pic, // 담당자 이름
        Date dueDate,
        Date uploadDate,
        Long picId
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getIsRequired(),
                task.getIsSubmitted(),
                task.getTitle(),
                task.getTaskType(),
                task.getFilePath(),
                task.getMember().getName(),
                task.getDueDate(),
                task.getUploadDate(),
                task.getMember().getId()
        );
    }
}
