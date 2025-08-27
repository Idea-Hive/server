package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.dto.response.ProjectTempSavedResponse;
import Idea.Idea_Hive.project.service.ProjectSearchService;
import Idea.Idea_Hive.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Slf4j
public class ProjectSearchController {

    private final ProjectSearchService projectSearchService;

    @Operation(summary = "프로젝트 검색")
    @GetMapping("/search")
    public ResponseEntity<ProjectSearchResponse> searchProjects(@RequestParam(required = false, defaultValue = "") String keyword,
                                                                @Parameter(schema = @Schema(allowableValues = {"NEW", "ADDITIONAL", "ALL"}))
                                                                @RequestParam(required = false, defaultValue = "ALL") String recruitType,
                                                                @Parameter(schema = @Schema(allowableValues = {"RECENT", "DEADLINE"}))
                                                                @RequestParam(required = false, defaultValue = "RECENT") String sortType,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "12") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);
        ProjectSearchResponse reponse = projectSearchService.searchProjects(keyword, recruitType, sortType, pageable);
        return ResponseEntity.ok(reponse);
    }

    @Operation(summary = "임시저장된 프로젝트 목록 조회")
    @GetMapping("/tempsaved")
    public ResponseEntity<List<ProjectTempSavedResponse>> tempSavedProjects(@RequestParam(required = true) Long userId) {
        List<ProjectTempSavedResponse> projects = projectSearchService.getTempSavedProjects(userId);
        return ResponseEntity.ok(projects);
    }

}
