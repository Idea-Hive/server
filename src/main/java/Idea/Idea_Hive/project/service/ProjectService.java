package Idea.Idea_Hive.project.service;


import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import Idea.Idea_Hive.project.dto.request.ProjectLikeRequest;
import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponse;
import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponseDto;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.entity.*;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberJpaRepo memberJpaRepo;

    @Transactional
    public ProjectInfoResponse getProjectInfo(Long projectId) {
        projectRepository.increaseViewCnt(projectId);
        ProjectInfoResponse projectInfoResponse = projectRepository.findProjectInfoById(projectId);
        return projectInfoResponse;
    }

    @Transactional
    public ProjectApplicantResponse getApplicantInfo(Long projectId, Pageable pageable) {
        Page<ProjectApplications> projectApplicantPage = projectRepository.findApplicantInfoById(projectId, pageable);
        return ProjectApplicantResponse.of(projectApplicantPage, projectRepository);
    }

    @Transactional
    public void likeProject(ProjectLikeRequest projectLikeRequest) {
        ProjectMemberId projectMemberId = ProjectMemberId.builder()
                .projectId(projectLikeRequest.getProjectId())
                .memberId(projectLikeRequest.getMemberId())
                .build();

        Project project = projectRepository.findById(projectLikeRequest.getProjectId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        Member member = memberJpaRepo.findById(projectLikeRequest.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Optional<ProjectMember> optionalProjectMember = projectMemberRepository.findById(projectMemberId);

        // 기존에 ProjectMember에 값이 없을 경우 새로운 값 추가
        if (optionalProjectMember.isEmpty()) {

            ProjectMember projectMember = ProjectMember.builder()
                    .id(projectMemberId)
                    .project(project)
                    .member(member)
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
            project.increaseLikedCnt();
        } else {
            project.decreaseLikedCnt();
        }
    }
}

