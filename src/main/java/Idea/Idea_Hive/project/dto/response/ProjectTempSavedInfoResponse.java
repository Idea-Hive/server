package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.project.entity.Project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ProjectTempSavedInfoResponse(
        String title,
        String description,
        String contact,
        Integer maxMembers,
        LocalDateTime dueDateFrom,
        LocalDateTime dueDateTo,
        List<String> hashtagNames,
        List<String> projectSkillStacks
) {
    public static ProjectTempSavedInfoResponse from(Project project) {
        return new ProjectTempSavedInfoResponse(
                project.getTitle(),
                project.getDescription(),
                project.getContact(),
                project.getMaxMembers(),
                project.getDueDateFrom(),
                project.getDueDateTo(),
                project.getHashtags().stream()
                        .map(Hashtag::getName)
                        .collect(Collectors.toList()),
                project.getProjectSkillStacks().stream()
                        .map(projectSkillStack -> projectSkillStack.getSkillstack().getName())
                        .collect(Collectors.toList())
        );
    }
}