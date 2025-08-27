package Idea.Idea_Hive.auth.dto.response;

public record LoginResponse(
        String email,
        String name,
        String accessToken
) {
}
