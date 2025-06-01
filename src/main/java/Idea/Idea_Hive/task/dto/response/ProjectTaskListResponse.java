package Idea.Idea_Hive.task.dto.response;

import Idea.Idea_Hive.task.entity.Task;

import java.util.List;

public record ProjectTaskListResponse(
        List<TaskResponse> requiredTasks, // 필수 과제 리스트
        List<TaskResponse> optionalTasks // 선택 과제 리스트
) {
}
