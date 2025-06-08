package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.request.ProjectSubmitRequest;
import Idea.Idea_Hive.project.service.ProjectManageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Slf4j
public class ProjectManageController {

    private final ProjectManageService projectManageService;

    @Operation(summary = "프로젝트 제출 API")
    @PostMapping("/submit")
    public ResponseEntity<Void> projectSubmit(@RequestBody ProjectSubmitRequest request) {
        projectManageService.submit(request.projectId());
        return ResponseEntity.ok().build();
    }
}
