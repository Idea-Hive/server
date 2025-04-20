package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProjectSearchResponse {
    private List<ProjectResponseDto> projects;
    private long totalCount;

    public static ProjectSearchResponse of(List<Project> projects) {
        List<ProjectResponseDto> projectDto = projects.stream()
                .map(ProjectResponseDto::from)
                .collect(Collectors.toList());
        return new ProjectSearchResponse(projectDto, projects.size());
    }
}
