package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public record ProjectSearchResponse (
        List<ProjectSearchResponseDto> projects,
        long totalCnt,
        int totalPages,
        int currentPage,
        int pageSize
){
    public static ProjectSearchResponse of(Page<Project> projectPage) {
        List<ProjectSearchResponseDto> projectDto = projectPage.getContent().stream()
                .map(ProjectSearchResponseDto::from)
                .collect(Collectors.toList());
        return new ProjectSearchResponse(
                projectDto,
                projectPage.getTotalElements(),
                projectPage.getTotalPages(),
                projectPage.getNumber()+1,
                projectPage.getSize());
    }
}
