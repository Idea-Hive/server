package Idea.Idea_Hive.task.dto.request;

public record UpdateTaskPicRequest(
        Long taskId,
        Long projectId,
        Long memberId // 변경할 member Id
) {
}
