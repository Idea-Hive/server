package Idea.Idea_Hive.task.dto.response;

import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.TaskType;

import java.time.LocalDateTime;
import java.util.Date;

public record TaskResponse(
        Long id,
        Boolean isRequired,
        Boolean isSubmitted,
        String title,
        TaskType taskType,
        String fileUploadLink,
        String originalFileName,
        String pic, // 담당자 이름
        LocalDateTime dueDate,
        Date fileUploadDate,
        Long picId
) {

    public static TaskResponse from(Task task) {

        String memberName = "";
        Long memberId = null;
        if(task.getMember() != null) {
            memberName = task.getMember().getName();
            memberId = task.getMember().getId();
        }
        assert task.getMember() != null;
        return new TaskResponse(
                task.getId(),
                task.getIsRequired(),
                task.getIsSubmitted(),
                task.getTitle(),
                task.getTaskType(),
                task.getAttachedLink(),
                task.getOriginalFileName(),
                memberName,
                task.getDueDate(),
                task.getFileUploadDate(),
                memberId
        );
    }
}
