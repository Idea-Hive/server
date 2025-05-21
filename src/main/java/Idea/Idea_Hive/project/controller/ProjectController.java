package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.request.ProjectLikeRequest;
import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponse;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/applicants")
    public ResponseEntity<ProjectApplicantResponse> applicantInfo(@RequestParam(required = true) Long projectId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        ProjectApplicantResponse projectApplicant = projectService.getApplicantInfo(projectId, pageable);
        return ResponseEntity.ok(projectApplicant);
    }

    @PostMapping("/like")
    public ResponseEntity<Void> projectLike(@RequestBody ProjectLikeRequest projectLikeRequest) {
        projectService.likeProject(projectLikeRequest);
        return ResponseEntity.ok().build();
    }
}
