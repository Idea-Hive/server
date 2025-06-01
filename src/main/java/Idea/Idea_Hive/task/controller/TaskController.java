package Idea.Idea_Hive.task.controller;

import Idea.Idea_Hive.task.dto.request.ProjectTaskListRequest;
import Idea.Idea_Hive.task.dto.response.ProjectTaskListResponse;
import Idea.Idea_Hive.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;


    @GetMapping("")
    public ResponseEntity<ProjectTaskListResponse> getTaskList(ProjectTaskListRequest request) {
        ProjectTaskListResponse response = taskService.getTaskList(request);
        return ResponseEntity.ok(response);
    }
}
