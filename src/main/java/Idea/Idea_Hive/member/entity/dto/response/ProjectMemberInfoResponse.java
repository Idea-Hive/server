package Idea.Idea_Hive.member.entity.dto.response;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.project.entity.Role;
import Idea.Idea_Hive.skillstack.entity.dto.SkillStackVO;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectMemberInfoResponse(
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
        Boolean isMarketingAgreed,
        Role projectRole
) {
    public static ProjectMemberInfoResponse from(Member member, Long projectId) {

        List<SkillStackVO> skillStacks = member.getMemberSkillStacks().stream()
                .map(ms -> SkillStackVO.from(ms.getSkillstack()))
                .toList();

        Role projectRole = member.getProjectMembers().stream()
                .filter(pm -> pm.getProject().getId().equals(projectId))
                .findFirst()
                .map(pm -> pm.getRole())
                .orElse(null);

        return new ProjectMemberInfoResponse(
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
                member.isMarketingAgreed(),
                projectRole
        );
    }
}
