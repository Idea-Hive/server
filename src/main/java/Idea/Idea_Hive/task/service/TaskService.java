package Idea.Idea_Hive.task.service;

import Idea.Idea_Hive.exception.handler.custom.FileStorageException;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.task.dto.request.*;
import Idea.Idea_Hive.task.dto.response.ProjectTaskListResponse;
import Idea.Idea_Hive.task.dto.response.TaskResponse;
import Idea.Idea_Hive.task.entity.ProjectTask;
import Idea.Idea_Hive.task.entity.ProjectTaskId;
import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.repository.ProjectTaskRepository;
import Idea.Idea_Hive.task.entity.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final ProjectTaskRepository projectTaskRepository;

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

    @Transactional
    public TaskResponse updateTaskDueDate(UpdateTaskDueDateRequest request) {
        // todo: Task 객체 불러오기
        Task task = taskRepository
                .findById(request.taskId())
                .orElseThrow(
                        () -> new BadCredentialsException("존재하지 않는 Task Id입니다.")
                );
        task.setDueDate(request.dueDate());
        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    @Transactional
    public TaskResponse updateTaskPic(UpdateTaskPicRequest request) {
        // todo: 변경할 담당자가 현재 project 멤버인지 체크
        // 1. 요청한 task 가져오기
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 taskId 입니다."));


        List<Member> membersInProject = memberRepository
                .findMemberByProject_with_querydsl(request.projectId());

        Member targetMember = membersInProject.stream()
                .filter(m -> m.getId().equals(request.memberId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 memberId는 프로젝트에 속해있지 않습니다."));

        task.setMember(targetMember);
        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    @Transactional
    public TaskResponse createOptionalTask(CreateOptionalTaskRequest request) {
        Task newTask = Task
                .builder()
                .taskType(request.taskType())
                .build();

        Task savedTask = taskRepository.save(newTask);

        ProjectTask projectTask = new ProjectTask();
        projectTask.setId(new ProjectTaskId(request.projectId(), savedTask.getId()));
        projectTaskRepository.save(projectTask);

        return TaskResponse.from(savedTask);
    }

    @Transactional
    public TaskResponse attachLink(AttachLinkRequest request) {
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new NullPointerException("존재하지 않는 과제입니다."));

        task.attachLink(request.attachedLink());
        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

}
