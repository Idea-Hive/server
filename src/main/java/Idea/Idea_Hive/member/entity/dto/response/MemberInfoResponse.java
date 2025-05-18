package Idea.Idea_Hive.member.entity.dto.response;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.MemberSkillStack;

import java.time.LocalDateTime;
import java.util.List;

public record MemberInfoResponse(
        Long id,
        String name,
        String email,
        LocalDateTime createdDate,
        String job,
        Integer career,
        String type,
        LocalDateTime modifiedDate,
        String profileUrl,
        List<MemberSkillStack> memberSkillStacks,
        Boolean isDeleted,
        Boolean isVerified
) {

    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getCreatedDate(),
                member.getJob(),
                member.getCareer(),
                member.getType(),
                member.getModifiedDate(),
                member.getProfileUrl(),
                member.getMemberSkillStacks(),
                member.getIsDeleted(),
                member.isVerified()
        );
    }
}
