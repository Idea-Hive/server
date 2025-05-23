package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.IsAccepted;
import Idea.Idea_Hive.project.entity.ProjectApplications;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectApplicantResponseDto {
    private Long memberId;
    private String name;
    private String job;
    private Integer career;
    private String applicationMessage;
    private List<String> skillStacks;
    private IsAccepted isAccepted;
    private Long completedProjectCnt;

    public static ProjectApplicantResponseDto from(ProjectApplications applications, Long completedProjectCnt) {
        ProjectApplicantResponseDto dto = new ProjectApplicantResponseDto();
        dto.memberId = applications.getMember().getId();
        dto.name = applications.getMember().getName();
        dto.job = applications.getMember().getJob();
        dto.career = applications.getMember().getCareer();
        dto.applicationMessage = applications.getApplicationMessage();
        dto.skillStacks = applications.getMember().getMemberSkillStacks().stream()
                .map(memberSkillStack -> memberSkillStack.getSkillstack().getName())
                .collect(Collectors.toList());
        dto.isAccepted = applications.getIsAccepted();
        dto.completedProjectCnt = completedProjectCnt;
        return dto;
    }

}
