package Idea.Idea_Hive.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
