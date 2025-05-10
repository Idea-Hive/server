package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.request.ProjectCreateRequest;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @GetMapping("/search")
    public ResponseEntity<ProjectSearchResponse> searchProjects(@RequestParam(required = false, defaultValue = "") String keyword,
                                                                @Parameter(schema = @Schema(allowableValues = {"NEW","ADDITIONAL","ALL"}))
                                                                @RequestParam(required = false, defaultValue = "ALL") String recruitType,
                                                                @Parameter(schema = @Schema(allowableValues = {"RECENT","DEADLINE"}))
                                                                @RequestParam(required = false, defaultValue = "RECENT") String sortType,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "12") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);
        ProjectSearchResponse reponse = projectService.searchProjects(keyword, recruitType, sortType, pageable);
        return ResponseEntity.ok(reponse);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createProject(@RequestBody ProjectCreateRequest projectCreateRequest) {
        Long projectId = projectService.createProject(projectCreateRequest);
        return ResponseEntity.ok(projectId);
    }
}
