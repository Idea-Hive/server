package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import Idea.Idea_Hive.project.dto.request.ProjectCreateRequest;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectDetail;
import Idea.Idea_Hive.project.entity.ProjectMember;
import Idea.Idea_Hive.project.entity.ProjectMemberId;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.project.entity.repository.SkillStackRepository;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static Idea.Idea_Hive.project.entity.Role.LEADER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillStackRepository skillStackRepository;
    private final MemberJpaRepo memberJpaRepo;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectSearchResponse searchProjects(String keyword, String recruitType) {
        List<Project> projects = projectRepository.searchByKeyword(keyword, recruitType);
        return ProjectSearchResponse.of(projects);
    }

    public Long createProject(ProjectCreateRequest request) {
        // 임시저장이 아닌 경우 validation 체크
        if (request.getIsSave()) {
            validationProjectRequest(request);
        }

        Member member = memberJpaRepo.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        //임시 저장 되어있던 프로젝트를 수정하여 저장하는 경우
        Project project;
        ProjectDetail projectDetail;

        if (request.getProjectId() != null) { //임시 저장했던 프로젝트가 있는 경우
            project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

            // 프로젝트 정보 업데이트
            project.updateTemporaryProject(
                    request.getTitle(),
                    request.getDescription(),
                    request.getContact(),
                    request.getMaxMembers(),
                    request.getDueDateFrom(),
                    request.getDueDateTo(),
                    request.getIsSave()
            );

            projectDetail = project.getProjectDetail();
            projectDetail.updateIdea(request.getIdea());
        } else { //새로 생성하는 경우
            projectDetail = ProjectDetail.builder()
                    .idea(request.getIdea())
                    .build();

            project = Project.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .contact(request.getContact())
                    .maxMembers(request.getMaxMembers())
                    .dueDateFrom(request.getDueDateFrom())
                    .dueDateTo(request.getDueDateTo())
                    .isSave(request.getIsSave())
                    .build();
        }

        // 연관관계 설정
        project.setProjectDetail(projectDetail);

        // 기술스택 추가
        if (request.getSkillStackIds() != null && !request.getSkillStackIds().isEmpty()) {
            //기존 기술스택 삭제(수정 시)
            if (request.getProjectId() != null) {
                project.getProjectSkillStacks().clear();
            }

            List<SkillStack> skillStacks = skillStackRepository.findAllById(request.getSkillStackIds());
            for (SkillStack skillStack : skillStacks) {
                project.addSkillStack(skillStack);
            }
        }

        //해시태그 추가
        if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {
            // 기존 해시태그 삭제 (수정 시)
            if (request.getProjectId() != null) {
                project.getHashtags().clear();
            }

            for (String hashtag : request.getHashtags()) {
                project.addHashtag(hashtag);
            }
        }

        Project savedProject = projectRepository.save(project);

        ProjectMemberId projectMemberId = ProjectMemberId.builder()
                .projectId(savedProject.getId())
                .memberId(member.getId())
                .build();

        Optional<ProjectMember> optionalProjectMember = projectMemberRepository.findById(projectMemberId);

        if (optionalProjectMember.isEmpty()) {
            log.info("ProjectMember if 문에 진입");
            ProjectMember projectMember = ProjectMember.builder()
                    .id(projectMemberId)
                    .role(LEADER)
                    .isProfileShared(true)
                    .profileSharedDate(LocalDateTime.now())
                    .isFavorited(false)
                    .build();

            projectMemberRepository.save(projectMember);
        }

        return savedProject.getId();
    }

    private void validationProjectRequest(ProjectCreateRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("제목은 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException("설명은 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(request.getIdea())) {
            throw new IllegalArgumentException("아이디어는 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(request.getContact())) {
            throw new IllegalArgumentException("연락처는 필수 입력값입니다.");
        }
        if (request.getMaxMembers() == null || request.getMaxMembers() <= 0) {
            throw new IllegalArgumentException("최대 인원은 1명 이상이어야 합니다.");
        }
    }
}
