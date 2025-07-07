package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.exception.handler.custom.InvalidProjectManageException;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.dto.response.MemberInfoResponse;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.project.dto.request.ProjectLeaveRequest;
import Idea.Idea_Hive.project.dto.response.MyPageProjectListResponse;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.entity.*;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.project.entity.repository.manage.ProjectManageRepository;
import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectManageService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final ProjectManageRepository projectManageRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public void submit(Long projectId) {
        // todo: Project 불러오기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        // todo: 제출에 필요한 조건 확인 후 예외처리하기
        /**
         * 1. 제출 권한 확인
         * 2. 과제 완료 했는지 확인(?) <-- 필수인지 검토 후 추가..
         * 3. 기타 제출 조건 확인
         */

        List<Task> tasks = taskRepository.findTasksByProjectId(projectId)
                .stream().filter(Task::getIsRequired).toList(); // 필수 제출만
        for (Task task : tasks) {
            if (!task.getIsSubmitted()) { // 제출 안한 경우 예외 던짐
                throw new IllegalArgumentException("제출하지 않은 항목이 있습니다 : " + task.getTitle());
            }
        }
        // todo: 상태 업데이트하기
        project.updateStatus(ProjectStatus.COMPLETED);
    }

    public ProjectSearchResponse getProjectListByStatus(Long memberId, ProjectStatus status, Pageable pageable) {
        Page<Project> projects = projectManageRepository.findProjectByMemberIdAndStatusWithPage(memberId, status, pageable);
        return ProjectSearchResponse.of(projects);
    }

    public MyPageProjectListResponse getAllProjectList(Long memberId, Pageable pageable) {
        Page<Project> projects = projectManageRepository.findProjectByMemberIdWithPage(memberId, pageable);
        return MyPageProjectListResponse.of(projects);
    }

    public List<MemberInfoResponse> getMembersByProjectId(Long projectId) {
        return projectManageRepository.findMemberByProjectId(projectId)
                .stream().map(MemberInfoResponse::from)
                .toList();
    }

    @Transactional
    public void leaveProject(Long memberId, Long projectId) {
        // 현재 로그인한 사람이 맞는지 검증
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 사용자가 아닙니다."));

        if (!member.getEmail().equals(memberEmail)) {
            throw new InvalidProjectManageException("로그인한 사용자와 탈퇴하려는 사용자 id가 다릅니다.");
        }

        // Project 찾기

        Project project = projectRepository.findById(projectId).orElseThrow( () -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
        // ProjectMember 생성
        ProjectMemberId projectMemberId = ProjectMemberId.builder()
                .projectId(project.getId())
                .memberId(member.getId())
                .build();

        Optional<ProjectMember> _projectMember = projectMemberRepository.findById(projectMemberId);

        if (_projectMember.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 프로젝트이거나, 해당 프로젝트의 멤버가 아닙니다.");
        }

        ProjectMember projectMember = _projectMember.get();
        if (projectMember.getRole().equals(Role.LEADER)) {
            if (getMembersByProjectId(projectId).size() != 1) {
                throw new IllegalArgumentException("현재 다른 팀원(들)이 존재하여 프로젝트 탈퇴가 불가능합니다.");
            }
            projectRepository.delete(project); // 팀장이 나가면 프로젝트가 아예 사라짐
        }
        projectMemberRepository.delete(projectMember);
    }
}
