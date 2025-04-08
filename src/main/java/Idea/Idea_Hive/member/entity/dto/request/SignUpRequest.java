package Idea.Idea_Hive.member.entity.dto.request;

public record SignUpRequest(
        String email,
        String password,
        String passwordCheck, // 이거 필요한가?
        String name,
        String job,
        String history,
        String interest,
        String type
) {
}
