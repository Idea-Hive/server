package Idea.Idea_Hive.member.entity.dto.request;

import java.util.List;

public record SignUpRequest(
        String email,
        String password,
        String passwordCheck,
        String name,
        String job,
        Integer career,
        String type, // sns 연동 종류
        List<Long> hashtagIds
) {
}
