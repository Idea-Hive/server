package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record MyPageProjectListResponse(
        Map<ProjectStatus, List<ProjectManageResponse>> projects,
        long totalCnt,
        int totalPages,
        int currentPage,
        int pageSize
) {
    public static MyPageProjectListResponse of(Page<Project> projectPage) {

        Map<ProjectStatus, List<ProjectManageResponse>> projectMap = projectPage.getContent().stream()
                .map(ProjectManageResponse::from)
                .collect(Collectors.groupingBy(
                        ProjectManageResponse::status,
                        Collectors.toList()
                ));

        return new MyPageProjectListResponse(
                projectMap,
                projectPage.getTotalElements(),
                projectPage.getTotalPages(),
                projectPage.getNumber()+1,
                projectPage.getSize());
    }
}
