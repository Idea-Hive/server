package Idea.Idea_Hive.task.dto.response;

import Idea.Idea_Hive.task.entity.Task;

import java.util.List;

public record ProjectTaskListResponse(
        List<Task> tasks
) {
}
