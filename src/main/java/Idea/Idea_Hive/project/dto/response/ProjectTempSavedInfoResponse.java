package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.project.entity.Project;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTempSavedInfoResponse {

    private String title;
    private String description;
    private String idea;
    private String contact;
    private Integer maxMembers;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
    private List<String> hashtagNames;
    private List<String> projectSkillStacks;

    public static ProjectTempSavedInfoResponse from(Project project) {
        ProjectTempSavedInfoResponse info = new ProjectTempSavedInfoResponse();
        info.title = project.getTitle();
        info.description = project.getDescription();
        info.idea = project.getProjectDetail().getIdea();
        info.contact = project.getContact();
        info.maxMembers = project.getMaxMembers();
        info.dueDateFrom = project.getDueDateFrom();
        info.dueDateTo = project.getDueDateTo();
        info.hashtagNames = project.getHashtags().stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());
        info.projectSkillStacks = project.getProjectSkillStacks().stream()
                .map(projectSkillStack -> projectSkillStack.getSkillstack().getName())
                .collect(Collectors.toList());
        return info;
    }
}
