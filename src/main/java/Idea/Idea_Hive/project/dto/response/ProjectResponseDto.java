package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectResponseDto {
    private Long id;
    private String title;
    private String description;

    public static ProjectResponseDto from(Project project) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.description = project.getDescription();
        return dto;
    }
}
