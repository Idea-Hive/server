package Idea.Idea_Hive.member.entity.dto.request;

import java.util.List;


/* todo: Optional<String> name 추가 */
public record SignUpRequest(
        String email,
        String password
) {
}
