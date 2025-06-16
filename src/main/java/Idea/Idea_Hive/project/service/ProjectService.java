package Idea.Idea_Hive.project.service;


import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import Idea.Idea_Hive.project.dto.request.*;
import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponse;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.entity.*;
import Idea.Idea_Hive.project.entity.repository.ProjectApplicationsRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.project.entity.repository.SkillStackRepository;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.notification.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberJpaRepo memberJpaRepo;
    private final ProjectApplicationsRepository projectApplicationsRepository;
    private final SkillStackRepository skillStackRepository;
    private final NotificationService notificationService;

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
    public void projectApplyDelete(Long applyId) {
        ProjectApplications projectApplications = projectApplicationsRepository.findById(applyId)
                .orElseThrow(() -> new IllegalArgumentException("지원한 내용이 없습니다."));

        Project project = projectApplications.getProject();
        project.getProjectApplications().remove(projectApplications);
        projectApplicationsRepository.delete(projectApplications);
    }

    @Transactional
    public void projectApplyUpdate(ProjectApplyUpdateRequest projectApplyUpdateRequest) {
        ProjectAndMemberInfo info = getProjectAndMemberInfo(projectApplyUpdateRequest.projectId(), projectApplyUpdateRequest.memberId());

        Optional<ProjectApplications> optionalProjectApplications = projectApplicationsRepository.findById(projectApplyUpdateRequest.applyId());

        if (optionalProjectApplications.isEmpty()) {
            throw new IllegalArgumentException("지원한 내용이 없습니다.");
        } else {
            optionalProjectApplications.get().updateApplicationMessage(projectApplyUpdateRequest.message());
        }
    }


    @Transactional
    public void projectApplyDecision(ProjectApplyDecisionRequest projectApplyDecisionRequest) {
        ProjectAndMemberInfo info = getProjectAndMemberInfo(projectApplyDecisionRequest.projectId(), projectApplyDecisionRequest.userId());

        Optional<ProjectApplications> optionalProjectApplications = projectApplicationsRepository.findById(projectApplyDecisionRequest.applyId());

        if (optionalProjectApplications.isEmpty()) {
            throw new IllegalArgumentException("지원한 내용이 없습니다.");
        } else {
            ProjectApplications projectApplications = optionalProjectApplications.get();
            projectApplications.updateIsAccepted(
                    projectApplyDecisionRequest.decision()
            );

            if (projectApplyDecisionRequest.decision() == IsAccepted.REJECTED) {
                projectApplications.updateRejectionMessage(projectApplyDecisionRequest.rejectionMessage());
            }

            Optional<ProjectMember> optionalProjectMember = projectMemberRepository.findById(info.getProjectMemberId());

            if (optionalProjectMember.isEmpty()) {
                throw new IllegalArgumentException("ProjectMember 테이블에 데이터가 존재하지 않습니다.");
            }

            if (projectApplyDecisionRequest.decision() == IsAccepted.CONFIRMED) {
                optionalProjectMember.get().updateRole(Role.TEAM_MEMBER);
            } else if (projectApplyDecisionRequest.decision() == IsAccepted.CANCEL_CONFIRM) { // 확정 취소의 경우 ProjectApplications의 isAccepted를 미정으로 변경
                optionalProjectMember.get().updateRole(Role.GUEST);
                optionalProjectApplications.get().updateIsAccepted(IsAccepted.UNDECIDED);
            }
        }
    }

    @Transactional
    public void applyProject(ProjectApplyRequest projectApplyRequest) {
        ProjectAndMemberInfo info = getProjectAndMemberInfo(projectApplyRequest.projectId(), projectApplyRequest.memberId());

        //이전 지원 기록 확인
        boolean isReApplication = projectApplicationsRepository.existsByProjectIdAndMemberId(
                projectApplyRequest.projectId(),
                projectApplyRequest.memberId()
        );

        //이전 지원 기록이 있는 경우 이전 거절 메시지 저장
        String preRejectionMessage = null;
        if (isReApplication) {
            preRejectionMessage = projectApplicationsRepository.findTopByProjectIdAndMemberIdOrderByApplicationDateDesc(
                            projectApplyRequest.projectId(),
                            projectApplyRequest.memberId()
                    ).map(ProjectApplications::getRejectionMessage)
                    .orElse(null);
        }

        //ProjectApplications 추가
        ProjectApplications newApplicant = ProjectApplications.builder()
                .project(info.getProject())
                .member(info.getMember())
                .applicationMessage(projectApplyRequest.message())
                .isAccepted(IsAccepted.UNDECIDED)
                .applicationDate(LocalDateTime.now())
                .isReApplication(isReApplication)
                .preRejectionMessage(preRejectionMessage)
                .build();

        //Project에 지원자 추가
        info.getProject().getProjectApplications().add(newApplicant);
        projectApplicationsRepository.save(newApplicant);

        Optional<ProjectMember> optionalProjectMember = projectMemberRepository.findById(info.getProjectMemberId());

        // 기존에 ProjectMember에 값이 없을 경우 새로운 값 추가
        if (optionalProjectMember.isEmpty()) {

            ProjectMember projectMember = ProjectMember.builder()
                    .id(info.getProjectMemberId())
                    .project(info.getProject())
                    .member(info.getMember())
                    .role(Role.GUEST)
                    .isProfileShared(true)
                    .profileSharedDate(LocalDateTime.now())
                    .isLike(false)
                    .build();

            projectMemberRepository.save(projectMember);

        } else{ // 기존에 ProjectMember에 값이 있을 경우
            optionalProjectMember.get().updateProfileShared(true);
        }

        //알람 발행
        notificationService.sendProjectApplicationNotification(
                projectApplyRequest.projectId(),
                info.getMember().getName()+"님이 프로젝트에 지원했습니다."
        );

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

        // 기존 지원 내역 삭제
        project.getProjectApplications().clear();

        project.updateIsNew(false);
        project.updateStatus(ProjectStatus.RECRUITING);
        project.updateModifiedDate(LocalDateTime.now());
        project.updateExpirationDate(LocalDateTime.now().plusMonths(1));
    }

    @Transactional
    public void startProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
        project.updateStatus(ProjectStatus.IN_PROGRESS);
    }

    @Transactional
    public ProjectInfoResponse getProjectInfo(Long projectId, Long userId) {
        ProjectInfoResponse projectInfoResponse = projectRepository.findProjectInfoById(projectId, userId);
        return projectInfoResponse;
    }

    @Transactional
    public void increaseViewCnt(Long projectId) {
        projectRepository.increaseViewCnt(projectId);
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

    // 끌어올리기
    @Transactional
    public void pushToTop(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        project.updateSearchDate(LocalDateTime.now());
        project.updateExpirationDate(LocalDateTime.now().plusMonths(1));
    }

    // 프로젝트 수정
    @Transactional
    public Long projectUpdate(ProjectUpdateRequest request) {

        //validation 체크
        validationProjectRequest(request);

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        //프로젝트 정보 수정
        project.updateProjectInfo(
                request.title(),
                request.description(),
                request.contact(),
                request.maxMembers(),
                request.dueDateFrom(),
                request.dueDateTo()
        );

        //기술스택 수정
        if (request.skillStackIds() == null || request.skillStackIds().isEmpty()) { //기술스택이 없을 경우 모두 삭제
            project.getProjectSkillStacks().clear();
        } else {
            List<SkillStack> skillStacks = skillStackRepository.findAllById(request.skillStackIds());
            if (skillStacks.size() != request.skillStackIds().size()) {
                throw new IllegalArgumentException("존재하지 않는 기술스택이 포함되어 있습니다.");
            }

            //기존 기술스택 중 요청에 없는 것들 삭제
            project.getProjectSkillStacks().removeIf(projectSkillStack ->
                    !request.skillStackIds().contains(projectSkillStack.getSkillstack().getId()));

            //새로운 기술스택 추가
            for (SkillStack skillStack : skillStacks) {
                boolean exists = project.getProjectSkillStacks().stream()
                        .anyMatch(ps -> ps.getSkillstack().getId().equals(skillStack.getId()));
                if (!exists) {
                    project.addSkillStack(skillStack);
                }
            }
        }

        // 해시태그 수정
        if (request.hashtags() == null || request.hashtags().isEmpty()) {
            project.getHashtags().clear();
        } else {
            //기존 해시태그 중 새로운 해시태그 목록에 없는 것만 삭제
            project.getHashtags().removeIf(hashtag ->
                    !request.hashtags().contains(hashtag.getName()));

            // 새로운 해시태그 중 기존에 없는 것만 추가
            for (String hashtagName : request.hashtags()) {
                boolean exists = project.getHashtags().stream()
                        .anyMatch(hashtag -> hashtag.getName().equals(hashtagName));
                if (!exists) {
                    project.addHashtag(hashtagName);
                }
            }
        }

        return project.getId();
    }

    private void validationProjectRequest(ProjectUpdateRequest request) {
        if (!StringUtils.hasText(request.title())) {
            throw new IllegalArgumentException("제목은 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(request.description())) {
            throw new IllegalArgumentException("설명은 필수 입력값입니다.");
        }
//        if (!StringUtils.hasText(request.idea())) {
//            throw new IllegalArgumentException("아이디어는 필수 입력값입니다.");
//        }
        if (!StringUtils.hasText(request.contact())) {
            throw new IllegalArgumentException("연락처는 필수 입력값입니다.");
        }
        if (request.maxMembers() == null || request.maxMembers() <= 0) {
            throw new IllegalArgumentException("최대 인원은 1명 이상이어야 합니다.");
        }
    }
}

