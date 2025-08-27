package Idea.Idea_Hive.member.entity.dto.response;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.MemberSkillStack;
import Idea.Idea_Hive.project.entity.Role;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.skillstack.entity.dto.SkillStackVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<SkillStackVO> SkillStacks,
        Boolean isDeleted,
        Boolean isServiceAgreed,
        Boolean isPrivacyAgreed,
        Boolean isMarketingAgreed
) {

    public static MemberInfoResponse from(Member member) {

        List<SkillStackVO> skillStacks = member.getMemberSkillStacks().stream()
                .map(ms -> SkillStackVO.from(ms.getSkillstack()))
                .toList();

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
                skillStacks,
                member.getIsDeleted(),
                member.isServiceAgreed(),
                member.isPrivacyAgreed(),
                member.isMarketingAgreed()
        );
    }
}
