package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record MyPageProjectResponse(
        Map<ProjectStatus, List<ProjectManageResponse>> projects,
        List<ProjectManageResponse> likeProject
) {
    // 찜한 프로젝트 목록 별도로 추가..
    public static MyPageProjectResponse of(List<Project> projects, List<Project> likeProjects) {

        Map<ProjectStatus, List<ProjectManageResponse>> projectMap = projects.stream()
                .map(ProjectManageResponse::from)
                .collect(Collectors.groupingBy(
                        ProjectManageResponse::status,
                        Collectors.toList()
                ));

        List<ProjectManageResponse> likes = likeProjects.stream()
                .map(ProjectManageResponse::from)
                .toList();

        return new MyPageProjectResponse(
                projectMap,
                likes);
    }
}
