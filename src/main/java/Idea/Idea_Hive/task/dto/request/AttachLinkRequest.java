package Idea.Idea_Hive.task.dto.request;

public record AttachLinkRequest(
        Long taskId,
        String attachedLink
) {
}
