package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.skillstack.entity.SkillStack;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicantDto {
    private Long memberId;
    private String name;
    private String job;
    private String career;
    private String applicationMessage;
    private List<SkillStack> skillStacks;
}
