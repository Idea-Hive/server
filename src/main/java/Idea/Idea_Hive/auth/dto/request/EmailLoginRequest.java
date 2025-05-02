package Idea.Idea_Hive.auth.dto.request;

public record EmailLoginRequest(
        String email,
        String rawPassword
) {
}
