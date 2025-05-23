package Idea.Idea_Hive.member.entity.dto.request;

import java.util.List;

public record SignUpRequest(
        String email,
        String password
) {
}
