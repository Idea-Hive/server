package Idea.Idea_Hive.project.controller;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.dto.response.MemberInfoResponse;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.project.dto.request.ProjectSubmitRequest;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.service.ProjectManageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Slf4j
public class ProjectManageController {

    private final ProjectManageService projectManageService;

    // todo: 수정 예정
    private final MemberRepository memberRepository;

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
        // 기본 size: 10, 정렬 기준: createdDate 내림차순
        Long memberId = memberRepository.findByEmail(email)
                .map(Member::getId)
                .orElseThrow(IllegalAccessError::new);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));

        ProjectSearchResponse response = projectManageService.getProjectListByStatus(memberId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary="프로젝트 멤버 조회 API")
    @GetMapping("/members")
    public ResponseEntity<List<MemberInfoResponse>> getProjectMemebers(@RequestParam("id") Long id) {
        return ResponseEntity.ok(
                projectManageService.getMembersByProjectId(id)
        );
    }
}
