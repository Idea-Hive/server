package Idea.Idea_Hive.project.dto.request;

public record ProjectLikeRequest(
        Long projectId,
        Long memberId,
        boolean like
) {}
