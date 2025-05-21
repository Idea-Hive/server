package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.IsAccepted;
import Idea.Idea_Hive.project.entity.ProjectApplications;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplicantDto {
    private Long memberId;
    private String name;
    private String job;
    private Integer career;
    private String applicationMessage;
    private List<String> skillStacks;
    private IsAccepted isAccepted;

    public static ApplicantDto from(ProjectApplications applications) {
        return ApplicantDto.builder()
                .memberId(applications.getMember().getId())
                .name(applications.getMember().getName())
                .job(applications.getMember().getJob())
                .career(applications.getMember().getCareer())
                .applicationMessage(applications.getApplicationMessage())
                .skillStacks(applications.getMember().getMemberSkillStacks().stream()
                        .map(memberSkillStack -> memberSkillStack.getSkillstack().getName())
                        .collect(Collectors.toList()))
                .isAccepted(applications.getIsAccepted())
                .build();
    }

}
