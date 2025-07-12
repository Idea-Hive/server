package Idea.Idea_Hive.project.dto.request;

public record ChangeProjectLeaderRequest(
        Long beforeLeaderId,
        Long afterLeaderId,
        Long projectId
) {
}
