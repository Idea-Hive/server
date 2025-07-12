package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.auth.service.AuthService;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.dto.response.MemberInfoResponse;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.project.dto.request.ChangeProjectLeaderRequest;
import Idea.Idea_Hive.project.dto.request.ProjectLeaveRequest;
import Idea.Idea_Hive.project.dto.request.ProjectSubmitRequest;
import Idea.Idea_Hive.project.dto.response.MyPageProjectResponse;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.service.ProjectManageService;
import Idea.Idea_Hive.task.dto.response.ProjectTaskListResponse;
import Idea.Idea_Hive.task.entity.TaskType;
import Idea.Idea_Hive.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Slf4j
public class ProjectManageController {

    private final ProjectManageService projectManageService;
    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final TaskService taskService;

    @Operation(summary = "프로젝트 제출 API")
    @PostMapping("/submit")
    public ResponseEntity<Void> projectSubmit(@RequestBody ProjectSubmitRequest request) {
        projectManageService.submit(request.projectId());
        return ResponseEntity.ok().build();
    }

    // todo: 전체적인 로직 수정할 예정
    @Operation(summary = "프로젝트 관리 > 프로젝트 목록 조회(본인) API")
    @GetMapping("/manage")
    public ResponseEntity<ProjectSearchResponse> getProject(
            @RequestParam("status") ProjectStatus status,
            @RequestParam("page") int page) {

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long memberId = memberRepository.findByEmail(email)
                .map(Member::getId)
                .orElseThrow(IllegalAccessError::new);

        // 기본 size: 10, 정렬 기준: createdDate 내림차순
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        ProjectSearchResponse response = projectManageService.getProjectListByStatus(memberId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary= "마이페이지 > 내 모든 프로젝트 목록 조회")
    @GetMapping("/all")
    public ResponseEntity<MyPageProjectResponse> getAllProject() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long memberId = memberRepository.findByEmail(email)
                .map(Member::getId)
                .orElseThrow(IllegalAccessError::new);

        MyPageProjectResponse response = projectManageService.getMyPageProject(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary="프로젝트 멤버 조회 API")
    @GetMapping("/members")
    public ResponseEntity<List<MemberInfoResponse>> getProjectMemebers(@RequestParam("id") Long id) {
        return ResponseEntity.ok(
                projectManageService.getMembersByProjectId(id)
        );
    }

    @Operation(summary="(캘린더) 프로젝트 모든 과제 조회 API")
    @GetMapping("/calender")
    public ResponseEntity<Map<TaskType, ProjectTaskListResponse>> getAllProjectTasks(
            @RequestParam("memberId") Long memberId,
            @RequestParam("projectId") Long projectId) {

        boolean verify = authService.verifyMemberIdIsLogined(memberId);
        if (!verify) {
            throw new BadCredentialsException("로그인한 사용자와 memberId가 다릅니다.");
        }
        Map<TaskType, ProjectTaskListResponse> projectTaskListResponseMap = taskService.getAllTask(projectId);

        return ResponseEntity.ok(projectTaskListResponseMap);
    }
      
     
    @Operation(summary = "프로젝트 탈퇴 API")
    @DeleteMapping("/leave")
    public ResponseEntity<String> projectLeave(@RequestBody ProjectLeaveRequest request) {
        projectManageService.leaveProject(request.memberId(), request.projectId());
        return ResponseEntity.ok("프로젝트 탈퇴 완료");
    }

    @Operation(summary = "프로젝트 삭제 API")
    @DeleteMapping("")
    public ResponseEntity<Void> projectDelete(@RequestBody ProjectLeaveRequest request) {
        projectManageService.deleteProject(request.memberId(), request.projectId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "팀장 변경 API")
    @PutMapping("/leader/change")
    public ResponseEntity<Void> changeProjectLeader(@RequestBody ChangeProjectLeaderRequest request) {
        return ResponseEntity.ok().build();
    }
}
