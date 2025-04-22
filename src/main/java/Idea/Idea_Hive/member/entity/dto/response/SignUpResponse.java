package Idea.Idea_Hive.member.entity.dto.response;

import Idea.Idea_Hive.member.entity.Member;

public record SignUpResponse(
        Long id,
        String email,
        String name
) {
    public static SignUpResponse from(Member member) {
        return new SignUpResponse(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
}
