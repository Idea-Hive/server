package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.project.dto.response.ProjectResponseDto;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/search")
    public ResponseEntity<ProjectSearchResponse> searchProjects(@RequestParam(required = false, defaultValue = "") String keyword,
                                                                @RequestParam(required = false, defaultValue = "ALL") String recruitType
                                                                ) {

        ProjectSearchResponse reponse = projectService.searchProjects(keyword,recruitType);
        return ResponseEntity.ok(reponse);

    }
}
