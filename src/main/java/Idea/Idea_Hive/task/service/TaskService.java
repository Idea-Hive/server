package Idea.Idea_Hive.task.service;

import Idea.Idea_Hive.task.dto.request.ProjectTaskListRequest;
import Idea.Idea_Hive.task.dto.response.ProjectTaskListResponse;
import Idea.Idea_Hive.task.dto.response.TaskResponse;
import Idea.Idea_Hive.task.entity.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public ProjectTaskListResponse getTaskList(ProjectTaskListRequest request) {

        Map<Boolean, List<TaskResponse>> partitioned = taskRepository
                .findTasksByProjectIdAndTaskType(request.projectId(), request.taskType())
                .stream()
                .map(TaskResponse::from)
                .collect(Collectors.partitioningBy(TaskResponse::isRequired));

        List<TaskResponse> requiredTasks = partitioned.getOrDefault(true, Collections.emptyList());
        List<TaskResponse> optionalTasks = partitioned.getOrDefault(false, Collections.emptyList());

        return new ProjectTaskListResponse(requiredTasks, optionalTasks);
    }
}
