package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ProjectTempSavedResponse {
    private Long projectId;
    private String title;
    private LocalDateTime tempSavedDate;

    public ProjectTempSavedResponse(Project project) {
        this.projectId = project.getId();
        this.title = project.getTitle();
        this.tempSavedDate = project.getCreatedDate();
    }

    public static List<ProjectTempSavedResponse> from(List<Project> projects) {
        return projects.stream()
                .map(ProjectTempSavedResponse::new)
                .collect(Collectors.toList());
    }
}
