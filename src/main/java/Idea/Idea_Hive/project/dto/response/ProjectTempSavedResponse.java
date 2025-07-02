package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.Project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ProjectTempSavedResponse(
        Long projectId,
        String name
) {
    public ProjectTempSavedResponse(Project project) {
        this(
                project.getId(),
                project.getName()
        );
    }

    public static List<ProjectTempSavedResponse> from(List<Project> projects) {
        return projects.stream()
                .map(ProjectTempSavedResponse::new)
                .collect(Collectors.toList());
    }
}