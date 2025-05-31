package Idea.Idea_Hive.project.service;


import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import Idea.Idea_Hive.project.dto.request.ProjectApplyDecisionRequest;
import Idea.Idea_Hive.project.dto.request.ProjectApplyRequest;
import Idea.Idea_Hive.project.dto.request.ProjectIdAndMemberIdDto;
import Idea.Idea_Hive.project.dto.request.ProjectLikeRequest;
import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponse;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.entity.*;
import Idea.Idea_Hive.project.entity.repository.ProjectApplicationsRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberJpaRepo memberJpaRepo;
    private final ProjectApplicationsRepository projectApplicationsRepository;

    /**
     * 프로젝트와 멤버 정보를 조회하는 유틸리티 메서드
     * @param projectId 프로젝트 ID
     * @param memberId  멤버 ID
     * @return ProjectAndMemberInfo 객체 (project, member, projectMemberId 포함)
     */
    private ProjectAndMemberInfo getProjectAndMemberInfo(Long projectId, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        Member member = memberJpaRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        ProjectMemberId projectMemberId = ProjectMemberId.builder()
                .projectId(projectId)
                .memberId(memberId)
                .build();

        return new ProjectAndMemberInfo(project, member, projectMemberId);

    }

    @Getter
    private static class ProjectAndMemberInfo{
        private final Project project;
        private final Member member;
        private final ProjectMemberId projectMemberId;

        public ProjectAndMemberInfo(Project project, Member member, ProjectMemberId projectMemberId) {
            this.project = project;
            this.member = member;
            this.projectMemberId = projectMemberId;
        }
    }

    @Transactional
    public void projectApplyDelete(ProjectIdAndMemberIdDto projectIdAndMemberIdDto) {
        ProjectAndMemberInfo info = getProjectAndMemberInfo(projectIdAndMemberIdDto.projectId(), projectIdAndMemberIdDto.memberId());

        Optional<ProjectApplications> optionalProjectApplications = projectApplicationsRepository.findById(info.getProjectMemberId());

        if (optionalProjectApplications.isEmpty()) {
            throw new IllegalArgumentException("지원한 내용이 없습니다.");
        } else {
            ProjectApplications projectApplications = optionalProjectApplications.get();
            info.getProject().getProjectApplications().remove(projectApplications);
            projectApplicationsRepository.delete(projectApplications);
        }
    }

    @Transactional
    public void projectApplyUpdate(ProjectApplyRequest projectApplyRequest) {
        ProjectAndMemberInfo info = getProjectAndMemberInfo(projectApplyRequest.projectId(), projectApplyRequest.memberId());

        Optional<ProjectApplications> optionalProjectApplications = projectApplicationsRepository.findById(info.getProjectMemberId());

        if (optionalProjectApplications.isEmpty()) {
            throw new IllegalArgumentException("지원한 내용이 없습니다.");
        } else {
            optionalProjectApplications.get().updateApplicationMessage(projectApplyRequest.message());
        }
    }


    @Transactional
    public void projectApplyDecision(ProjectApplyDecisionRequest projectApplyDecisionRequest) {
        ProjectAndMemberInfo info = getProjectAndMemberInfo(projectApplyDecisionRequest.projectId(), projectApplyDecisionRequest.memberId());

        Optional<ProjectApplications> optionalProjectApplications = projectApplicationsRepository.findById(info.getProjectMemberId());

        if (optionalProjectApplications.isEmpty()) {
            throw new IllegalArgumentException("지원한 내용이 없습니다.");
        } else {
            optionalProjectApplications.get().updateIsAcceptedAndRejectMessage(
                    projectApplyDecisionRequest.decision(),
                    projectApplyDecisionRequest.rejectionMessage());
        }
    }

    @Transactional
    public void applyProject(ProjectApplyRequest projectApplyRequest) {
        ProjectAndMemberInfo info = getProjectAndMemberInfo(projectApplyRequest.projectId(), projectApplyRequest.memberId());

        Optional<ProjectApplications> optionalProjectApplications = projectApplicationsRepository.findById(info.getProjectMemberId());

        if (optionalProjectApplications.isPresent()) {
            throw new IllegalArgumentException("이미 지원한 프로젝트입니다.");
        } else {
            info.getProject().addProjectApplications(
                    info.getProjectMemberId(),
                    info.getMember(),
                    projectApplyRequest.message(),
                    IsAccepted.UNDECIDED);
        }
    }

    @Transactional
    public void viewIdea(ProjectIdAndMemberIdDto projectIdAndMemberIdDto) {
        ProjectAndMemberInfo projectMemberInfo = getProjectAndMemberInfo(projectIdAndMemberIdDto.projectId(), projectIdAndMemberIdDto.memberId());

        Optional<ProjectMember> optionalProjectMember = projectMemberRepository.findById(projectMemberInfo.getProjectMemberId());

        if (optionalProjectMember.isEmpty()) {

            ProjectMember projectMember = ProjectMember.builder()
                    .id(projectMemberInfo.getProjectMemberId())
                    .project(projectMemberInfo.getProject())
                    .member(projectMemberInfo.getMember())
                    .role(Role.GUEST)
                    .isProfileShared(true)
                    .profileSharedDate(LocalDateTime.now())
                    .isLike(false)
                    .build();

            projectMemberRepository.save(projectMember);
        } else {
            optionalProjectMember.get().updateProfileShared(true);
        }
    }
    @Transactional
    public void recruitMember(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        project.updateIsNew(false);
        project.updateStatus(ProjectStatus.RECRUITING);
    }

    @Transactional
    public void startProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
        project.updateStatus(ProjectStatus.IN_PROGRESS);
    }

    @Transactional
    public ProjectInfoResponse getProjectInfo(Long projectId, Long userId) {
        projectRepository.increaseViewCnt(projectId);
        ProjectInfoResponse projectInfoResponse = projectRepository.findProjectInfoById(projectId, userId);
        return projectInfoResponse;
    }

    @Transactional
    public ProjectApplicantResponse getApplicantInfo(Long projectId, Pageable pageable) {
        Page<ProjectApplications> projectApplicantPage = projectRepository.findApplicantInfoById(projectId, pageable);
        return ProjectApplicantResponse.of(projectApplicantPage, projectRepository);
    }

    @Transactional
    public void likeProject(ProjectLikeRequest projectLikeRequest) {
        ProjectAndMemberInfo projectMemberInfo = getProjectAndMemberInfo(projectLikeRequest.projectId(), projectLikeRequest.memberId());

        Optional<ProjectMember> optionalProjectMember = projectMemberRepository.findById(projectMemberInfo.getProjectMemberId());

        // 기존에 ProjectMember에 값이 없을 경우 새로운 값 추가
        if (optionalProjectMember.isEmpty()) {

            ProjectMember projectMember = ProjectMember.builder()
                    .id(projectMemberInfo.getProjectMemberId())
                    .project(projectMemberInfo.getProject())
                    .member(projectMemberInfo.getMember())
                    .role(Role.GUEST)
                    .isProfileShared(false)
                    .profileSharedDate(null)
                    .isLike(projectLikeRequest.isLike())
                    .build();

            projectMemberRepository.save(projectMember);
        }else{ // 기존에 ProjectMember에 값이 있을 경우
            optionalProjectMember.get().updateLike(projectLikeRequest.isLike());
        }

        if (projectLikeRequest.isLike()) {
            projectMemberInfo.getProject().increaseLikedCnt();
        } else {
            projectMemberInfo.getProject().decreaseLikedCnt();
        }
    }
}

