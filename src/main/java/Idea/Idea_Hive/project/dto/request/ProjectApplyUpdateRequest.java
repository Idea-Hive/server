package Idea.Idea_Hive.project.dto.request;

public record ProjectApplyUpdateRequest(
        Long projectId,
        Long memberId,
        Long applyId,
        String message) {
}
