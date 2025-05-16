package Idea.Idea_Hive.member.entity.dto.request;

public record PasswordResetRequest(
        String email,
        String newPassword
) {
}
