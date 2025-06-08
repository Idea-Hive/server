package Idea.Idea_Hive.task.controller;

import Idea.Idea_Hive.task.dto.request.CreateOptionalTaskRequest;
import Idea.Idea_Hive.task.dto.request.ProjectTaskListRequest;
import Idea.Idea_Hive.task.dto.request.UpdateTaskDueDateRequest;
import Idea.Idea_Hive.task.dto.request.UpdateTaskPicRequest;
import Idea.Idea_Hive.task.dto.response.ProjectTaskListResponse;
import Idea.Idea_Hive.task.dto.response.TaskResponse;
import Idea.Idea_Hive.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PutMapping("/duedate")
    public ResponseEntity<TaskResponse> updateTaskDueDate(@RequestBody  UpdateTaskDueDateRequest request) {
        TaskResponse response = taskService.updateTaskDueDate(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/pic")
    public ResponseEntity<TaskResponse> updateTaskPic(@RequestBody UpdateTaskPicRequest request) {
        TaskResponse response = taskService.updateTaskPic(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("option")
    public ResponseEntity<TaskResponse> createOptionalTask(@RequestBody CreateOptionalTaskRequest request) {
        TaskResponse response = taskService.createOptionalTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
