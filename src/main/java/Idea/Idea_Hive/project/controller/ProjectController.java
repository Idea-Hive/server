package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.request.ProjectCreateRequest;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/info") //프로젝트 상세 조회
    public ResponseEntity<ProjectInfoResponse> projectInfo(@RequestParam(required = true) Long projectId) {
        ProjectInfoResponse projectInfoResponse = projectService.getProjectInfo(projectId);
        return ResponseEntity.ok(projectInfoResponse);
    }
}
