package Idea.Idea_Hive.project.dto.request;

public record ProjectApplyRequest(
        Long projectId,
        Long memberId,
        String message
) {}
