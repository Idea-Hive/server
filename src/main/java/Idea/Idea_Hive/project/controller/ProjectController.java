package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.request.ProjectLikeRequest;
import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponse;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.service.ProjectService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    //프로젝트 상세 조회
    @GetMapping("/info")
    public ResponseEntity<ProjectInfoResponse> projectInfo(@RequestParam(required = true) Long projectId) {
        ProjectInfoResponse projectInfoResponse = projectService.getProjectInfo(projectId);
        return ResponseEntity.ok(projectInfoResponse);
    }

    // 지원자 정보 조회
    @GetMapping("/applicants")
    public ResponseEntity<ProjectApplicantResponse> applicantInfo(@RequestParam(required = true) Long projectId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        ProjectApplicantResponse projectApplicant = projectService.getApplicantInfo(projectId, pageable);
        return ResponseEntity.ok(projectApplicant);
    }

    // 찜하기, 찜 해제
    @PostMapping("/like")
    public ResponseEntity<Void> projectLike(@RequestBody ProjectLikeRequest projectLikeRequest) {
        projectService.likeProject(projectLikeRequest);
        return ResponseEntity.ok().build();
    }

    //프로젝트 시작
    @PostMapping("/start")
    public ResponseEntity<Void> projectStart(@RequestBody @Schema(example = "{\"projectId\":1}") Map<String, Long> projectId) {
        projectService.startProject(projectId.get("projectId"));
        return ResponseEntity.ok().build();
    }

    //팀원 추가모집
    @PostMapping("/recruit")
    public ResponseEntity<Void> memberRecruit(@RequestBody @Schema(example = "{\"projectId\":1}") Map<String, Long> projectId) {
        projectService.recruitMember(projectId.get("projectId"));
        return ResponseEntity.ok().build();
    }
}













