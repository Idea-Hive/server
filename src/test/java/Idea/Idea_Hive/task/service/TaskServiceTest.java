package Idea.Idea_Hive.task.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.task.dto.request.CreateOptionalTaskRequest;
import Idea.Idea_Hive.task.dto.request.UpdateTaskDueDateRequest;
import Idea.Idea_Hive.task.dto.request.UpdateTaskPicRequest;
import Idea.Idea_Hive.task.dto.response.ProjectTaskListResponse;
import Idea.Idea_Hive.task.dto.response.TaskResponse;
import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.TaskType;
import Idea.Idea_Hive.task.entity.repository.ProjectTaskRepository;
import Idea.Idea_Hive.task.entity.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectTaskRepository projectTaskRepository;

    @Mock
    private ProjectRepository projectRepository;

    private Task task;
    private Member member;
    private Project project;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = Member.builder().name("testuser").build();
        Field memberIdField = member.getClass().getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(member, 1L);

        project = Project.builder().name("testproject").build();
        task = Task.builder()
                .title("Test Task")
                .isRequired(true)
                .taskType(TaskType.PLANNING)
                .build();
        task.setMember(member);
    }

    @Test
    @DisplayName("프로젝트의 과제 목록을 조회합니다.")
    void getTaskList() {
        // given
        when(taskRepository.findTasksByProjectIdAndTaskType(1L, TaskType.PLANNING))
                .thenReturn(Collections.singletonList(task));

        // when
        ProjectTaskListResponse taskList = taskService.getTaskList(1L, TaskType.PLANNING);

        // then
        assertThat(taskList.requiredTasks()).hasSize(1);
        assertThat(taskList.requiredTasks().get(0).title()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("과제 마감일을 수정합니다.")
    void updateTaskDueDate() {
        // given
        LocalDateTime newDueDate = LocalDateTime.now().plusDays(7);
        UpdateTaskDueDateRequest request = new UpdateTaskDueDateRequest(1L, newDueDate);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        TaskResponse updatedTask = taskService.updateTaskDueDate(request);

        // then
        assertThat(updatedTask.dueDate()).isEqualTo(newDueDate);
    }

    @Test
    @DisplayName("과제 담당자를 수정합니다.")
    void updateTaskPic() throws NoSuchFieldException, IllegalAccessException {
        // given
        Member newMember = Member.builder().name("newuser").build();
        Field memberIdField = newMember.getClass().getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(newMember, 2L);

        UpdateTaskPicRequest request = new UpdateTaskPicRequest(1L, 1L, 2L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(memberRepository.findMemberByProject_with_querydsl(1L)).thenReturn(Collections.singletonList(newMember));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        TaskResponse updatedTask = taskService.updateTaskPic(request);

        // then
        assertThat(updatedTask.pic()).isEqualTo(newMember.getName());
    }

    @Test
    @DisplayName("선택 과제를 생성합니다.")
    void createOptionalTask() {
        // given
        CreateOptionalTaskRequest request = new CreateOptionalTaskRequest(1L, TaskType.PLANNING);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // when
        TaskResponse createdTask = taskService.createOptionalTask(request);

        // then
        assertThat(createdTask.title()).isEqualTo("Test Task");
    }
}