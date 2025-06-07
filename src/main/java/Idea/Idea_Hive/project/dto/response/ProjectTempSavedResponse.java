package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.Project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ProjectTempSavedResponse(
        Long projectId,
        String title,
        LocalDateTime tempSavedDate
) {
    public ProjectTempSavedResponse(Project project) {
        this(
                project.getId(),
                project.getTitle(),
                project.getCreatedDate()
        );
    }

    public static List<ProjectTempSavedResponse> from(List<Project> projects) {
        return projects.stream()
                .map(ProjectTempSavedResponse::new)
                .collect(Collectors.toList());
    }
}