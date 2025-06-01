package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.request.ProjectApplyDecisionRequest;
import Idea.Idea_Hive.project.dto.request.ProjectApplyRequest;
import Idea.Idea_Hive.project.dto.request.ProjectIdAndMemberIdDto;
import Idea.Idea_Hive.project.dto.request.ProjectLikeRequest;
import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponse;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "프로젝트 상세 조회")
    @GetMapping("/info")
    public ResponseEntity<ProjectInfoResponse> projectInfo(@RequestParam(required = true) Long projectId,
                                                           @RequestParam(required = false) Long userId) {
        ProjectInfoResponse projectInfoResponse = projectService.getProjectInfo(projectId, userId);
        return ResponseEntity.ok(projectInfoResponse);
    }

    @Operation(summary = "지원자 정보 조회")
    @GetMapping("/applicants")
    public ResponseEntity<ProjectApplicantResponse> applicantInfo(@RequestParam(required = true) Long projectId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        ProjectApplicantResponse projectApplicant = projectService.getApplicantInfo(projectId, pageable);
        return ResponseEntity.ok(projectApplicant);
    }

    @Operation(summary = "찜하기, 찜 해제")
    @PostMapping("/like")
    public ResponseEntity<Void> projectLike(@RequestBody ProjectLikeRequest projectLikeRequest) {
        projectService.likeProject(projectLikeRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로젝트 시작")
    @PostMapping("/start")
    public ResponseEntity<Void> projectStart(@RequestBody @Schema(example = "{\"projectId\":1}") Map<String, Long> projectId) {
        projectService.startProject(projectId.get("projectId"));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "팀원 추가모집")
    @PostMapping("/recruit")
    public ResponseEntity<Void> memberRecruit(@RequestBody @Schema(example = "{\"projectId\":1}") Map<String, Long> projectId) {
        projectService.recruitMember(projectId.get("projectId"));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "아이디어 열람")
    @PostMapping("/view")
    public ResponseEntity<Void> viewIdea(@RequestBody ProjectIdAndMemberIdDto projectIdAndMemberIdDto) {
        projectService.viewIdea(projectIdAndMemberIdDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "지원하기")
    @PostMapping("/apply")
    public ResponseEntity<Void> projectApply(@RequestBody ProjectApplyRequest projectApplyRequest) {
        projectService.applyProject(projectApplyRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "지원자 거절/확정/확정취소", description = "decision 값은 CONFIRMED(확정), REJECTED(거절), CANCEL_CONFIRM(확정취소) 가능")
    @PostMapping("/apply/decision")
    public ResponseEntity<Void> projectApplyDecision(@RequestBody ProjectApplyDecisionRequest projectApplyDecisionRequest) {
        projectService.projectApplyDecision(projectApplyDecisionRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "지원하기 수정")
    @PostMapping("/apply/update")
    public ResponseEntity<Void> projectApplyUpdate(@RequestBody ProjectApplyRequest projectApplyRequest) {
        projectService.projectApplyUpdate(projectApplyRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "지원취소")
    @DeleteMapping("/apply")
    public ResponseEntity<Void> projectApplyDelete(@RequestBody ProjectIdAndMemberIdDto projectIdAndMemberIdDto) {
        projectService.projectApplyDelete(projectIdAndMemberIdDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "조회수 count")
    @PostMapping("/viewCnt")
    public ResponseEntity<Void> projectIncreaseViewCnt(@RequestBody @Schema(example = "{\"projectId\":1}") Map<String, Long> projectId) {
        projectService.increaseViewCnt(projectId.get("projectId"));
        return ResponseEntity.ok().build();
    }
}













