package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectInfoResponse {
    private String title;
    private List<Hashtag> hashtags;
    private String creator;
    private List<SkillStack> creatorSkillStack;
    private String description;
    private String idea;
    private Integer maxMembers;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
    private String contact;
    private List<ApplicantDto> applicants;
    private Long creatorId;

}
