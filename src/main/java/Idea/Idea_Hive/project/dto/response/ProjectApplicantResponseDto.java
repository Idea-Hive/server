package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.IsAccepted;
import Idea.Idea_Hive.project.entity.ProjectApplications;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public record ProjectApplicantResponseDto (
        Long applyId,
        Long memberId,
        String name,
        String job,
        Integer career,
        String applicationMessage,
        List<String> skillStacks,
        IsAccepted isAccepted,
        Long completedProjectCnt,
        String rejectionMessage,
        Boolean isReApplication,
        String preRejectionMessage
){


    public static ProjectApplicantResponseDto from(ProjectApplications applications, Long completedProjectCnt) {
        return new ProjectApplicantResponseDto(
                applications.getId(),
                applications.getMember().getId(),
                applications.getMember().getName(),
                applications.getMember().getJob(),
                applications.getMember().getCareer(),
                applications.getApplicationMessage(),
                applications.getMember().getMemberSkillStacks().stream()
                        .map(memberSkillStack -> memberSkillStack.getSkillstack().getName())
                        .collect(Collectors.toList()),
                applications.getIsAccepted(),
                completedProjectCnt,
                applications.getRejectionMessage(),
                applications.getIsReApplication(),
                applications.getPreRejectionMessage()
        );
    }
}
