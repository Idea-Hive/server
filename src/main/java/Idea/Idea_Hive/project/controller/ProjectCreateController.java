package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.request.ProjectCreateRequest;
import Idea.Idea_Hive.project.dto.response.ProjectTempSavedInfoResponse;
import Idea.Idea_Hive.project.service.ProjectCreateService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Slf4j
public class ProjectCreateController {

    private final ProjectCreateService projectCreateService;

    @Operation(summary = "프로젝트 생성")
    @PostMapping("/create")
    public ResponseEntity<Long> createProject(@RequestBody ProjectCreateRequest projectCreateRequest) {
        Long projectId = projectCreateService.createProject(projectCreateRequest);
        return ResponseEntity.ok(projectId);
    }

    @Operation(summary = "임시저장된 프로젝트 정보 조회")
    @GetMapping("/tempsaved/info")
    public ResponseEntity<ProjectTempSavedInfoResponse> tempSavedProjectInfo(@RequestParam(required = true) Long projectId) {
        ProjectTempSavedInfoResponse info = projectCreateService.getTempSavedProjectInfo(projectId);
        return ResponseEntity.ok(info);
    }
}
