package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.project.dto.request.ChangeProjectLeaderRequest;
import Idea.Idea_Hive.project.dto.response.ProjectSubmitResponse;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectMember;
import Idea.Idea_Hive.project.entity.Role;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.project.entity.repository.manage.ProjectManageRepository;
import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectManageServiceTest {

    @InjectMocks
    private ProjectManageService projectManageService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectManageRepository projectManageRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    private Project project;
    private Member member;
    private ProjectMember projectMember;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        project = Project.builder().name("testproject").build();
        member = Member.builder().email("test@test.com").build();
        // Set ID for member using reflection
        Field memberIdField = member.getClass().getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(member, 1L);

        projectMember = ProjectMember.builder().project(project).member(member).role(Role.LEADER).build();
    }

    @Test
    @DisplayName("프로젝트를 성공적으로 제출합니다.")
    void submitProject() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.findTasksByProjectId(1L)).thenReturn(Collections.emptyList());

        // when
        ProjectSubmitResponse response = projectManageService.submit(1L);

        // then
        assertThat(response.isAllSubmitted()).isTrue();
    }

    @Test
    @DisplayName("제출되지 않은 과제가 있을 경우 프로젝트 제출에 실패합니다.")
    void submitProjectWithUnsubmittedTasks() {
        // given
        Task unsubmittedTask = Task.builder().isRequired(true).isSubmitted(false).build();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.findTasksByProjectId(1L)).thenReturn(List.of(unsubmittedTask));

        // when
        ProjectSubmitResponse response = projectManageService.submit(1L);

        // then
        assertThat(response.isAllSubmitted()).isFalse();
        assertThat(response.unsubmittedTaskIds()).hasSize(1);
    }

    @Test
    @DisplayName("프로젝트에서 성공적으로 탈퇴합니다.")
    void leaveProject() {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList())
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndMemberId(1L, 1L)).thenReturn(Optional.of(projectMember));
        doNothing().when(projectMemberRepository).delete(projectMember);
        when(projectManageRepository.findMemberByProjectId(1L)).thenReturn(Collections.singletonList(member));

        // when
        projectManageService.leaveProject(1L, 1L);

        // then
        verify(projectMemberRepository, times(1)).delete(projectMember);
    }

    @Test
    @DisplayName("프로젝트를 성공적으로 삭제합니다.")
    void deleteProject() {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList())
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndMemberId(1L, 1L)).thenReturn(Optional.of(projectMember));
        when(projectManageRepository.findMemberByProjectId(1L)).thenReturn(Collections.singletonList(member));
        doNothing().when(projectRepository).delete(project);

        // when
        projectManageService.deleteProject(1L, 1L);

        // then
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    @DisplayName("팀장이 아닌 경우 프로젝트 삭제 시 예외가 발생합니다.")
    void deleteProjectWhenNotLeader() {
        // given
        projectMember.setRole(Role.TEAM_MEMBER);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList())
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndMemberId(1L, 1L)).thenReturn(Optional.of(projectMember));

        // when & then
        assertThatThrownBy(() -> projectManageService.deleteProject(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("팀장만 삭제 가능합니다.");
    }

    @Test
    @DisplayName("프로젝트 팀장을 성공적으로 변경합니다.")
    void changeProjectLeader() throws NoSuchFieldException, IllegalAccessException {
        // given
        Member afterMember = Member.builder().email("after@test.com").build();
        // Set ID for afterMember using reflection
        Field afterMemberIdField = afterMember.getClass().getDeclaredField("id");
        afterMemberIdField.setAccessible(true);
        afterMemberIdField.set(afterMember, 2L);

        ProjectMember afterProjectMember = ProjectMember.builder().project(project).member(afterMember).role(Role.TEAM_MEMBER).build();
        ChangeProjectLeaderRequest request = new ChangeProjectLeaderRequest(1L, 1L, 2L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList())
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(afterMember));
        when(projectMemberRepository.findByProjectIdAndMemberId(1L, 1L)).thenReturn(Optional.of(projectMember));
        when(projectMemberRepository.findByProjectIdAndMemberId(1L, 2L)).thenReturn(Optional.of(afterProjectMember));

        // when
        projectManageService.changeProjectLeader(request);

        // then
        assertThat(projectMember.getRole()).isEqualTo(Role.TEAM_MEMBER);
        assertThat(afterProjectMember.getRole()).isEqualTo(Role.LEADER);
    }
}