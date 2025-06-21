package Idea.Idea_Hive.task.controller;

import Idea.Idea_Hive.file.service.FileStorageService;
import Idea.Idea_Hive.task.dto.request.*;
import Idea.Idea_Hive.task.dto.response.ProjectTaskListResponse;
import Idea.Idea_Hive.task.dto.response.TaskResponse;
import Idea.Idea_Hive.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;
    private final FileStorageService fileStorageService;


    @GetMapping("")
    public ResponseEntity<ProjectTaskListResponse> getTaskList(ProjectTaskListRequest request) {
        ProjectTaskListResponse response = taskService.getTaskList(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "종료일 설정 API")
    @PutMapping("/duedate")
    public ResponseEntity<TaskResponse> updateTaskDueDate(@RequestBody  UpdateTaskDueDateRequest request) {
        TaskResponse response = taskService.updateTaskDueDate(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "담당자 변경 API")
    @PutMapping("/pic")
    public ResponseEntity<TaskResponse> updateTaskPic(@RequestBody UpdateTaskPicRequest request) {
        TaskResponse response = taskService.updateTaskPic(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "선택 과제 추가 API")
    @PostMapping("/option")
    public ResponseEntity<TaskResponse> createOptionalTask(@RequestBody CreateOptionalTaskRequest request) {
        TaskResponse response = taskService.createOptionalTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로컬용 과제 파일 업로드 API - 개발 중")
    @PostMapping(value = "/file-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> uploadTaskFile(
            @Parameter(description = "업로드할 파일", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("file") MultipartFile file,

            @Parameter(description = "Task id", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("taskInfo") FileUploadRequest request
            ) {

        fileStorageService.storeFile(file, request.taskId());
        return ResponseEntity.ok("개발중입니다.");
    }
}
