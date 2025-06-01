package Idea.Idea_Hive.task.service;

import Idea.Idea_Hive.task.entity.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

}
