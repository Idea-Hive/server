package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import Idea.Idea_Hive.project.dto.request.ProjectCreateRequest;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.entity.*;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.project.entity.repository.SkillStackRepository;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Optional;

import static Idea.Idea_Hive.project.entity.Role.LEADER;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillStackRepository skillStackRepository;
    private final MemberJpaRepo memberJpaRepo;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional(readOnly = true)
    public ProjectSearchResponse searchProjects(String keyword, String recruitType, String sortType, Pageable pageable) {
        Page<Project> projectPage = projectRepository.searchByKeyword(keyword, recruitType, sortType, pageable);
        return ProjectSearchResponse.of(projectPage);
    }

    @Transactional
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


        // 입력된 기술스택 유효성 검사


        // 기술스택 추가
        if (request.getProjectId() != null) { //기존에 저장되어있는 프로젝트가 있을 경우
            if (request.getSkillStackIds() == null || request.getSkillStackIds().isEmpty()) { //기술스택이 없을 경우 모두 삭제
                project.getProjectSkillStacks().clear();
            } else {
                List<SkillStack> skillStacks = skillStackRepository.findAllById(request.getSkillStackIds());
                if (skillStacks.size() != request.getSkillStackIds().size()) {
                    throw new IllegalArgumentException("존재하지 않는 기술스택이 포함되어 있습니다.");
                }

                //기존 기술스택 중 요청에 없는 것들 삭제
                project.getProjectSkillStacks().removeIf(projectSkillStack ->
                        !request.getSkillStackIds().contains(projectSkillStack.getSkillstack().getId()));

                //새로운 기술스택 추가
                for (SkillStack skillStack : skillStacks) {
                    boolean exists = project.getProjectSkillStacks().stream()
                            .anyMatch(ps -> ps.getSkillstack().getId().equals(skillStack.getId()));
                    if (!exists) {
                        project.addSkillStack(skillStack);
                    }
                }
            }
        } else if (request.getSkillStackIds() != null && !request.getSkillStackIds().isEmpty()) { //새 프로젝트 생성 시 기술스택 추가

            List<SkillStack> skillStacks = skillStackRepository.findAllById(request.getSkillStackIds());
            if (skillStacks.size() != request.getSkillStackIds().size()) {
                throw new IllegalArgumentException("존재하지 않는 기술스택이 포함되어 있습니다.");
            }

            for (SkillStack skillStack : skillStacks) {
                project.addSkillStack(skillStack);
            }
        }

        //해시태그 추가
        // 기존에 저장되어있는 프로젝트가 있을 경우
        if (request.getProjectId() != null) {
            if (request.getHashtags() == null || request.getHashtags().isEmpty()) {
                project.getHashtags().clear();
            } else {
                //기존 해시태그 중 새로운 해시태그 목록에 없는 것만 삭제
                project.getHashtags().removeIf(hashtag ->
                        !request.getHashtags().contains(hashtag.getName()));

                // 새로운 해시태그 중 기존에 없는 것만 추가
                for (String hashtagName : request.getHashtags()) {
                    boolean exists = project.getHashtags().stream()
                            .anyMatch(hashtag -> hashtag.getName().equals(hashtagName));
                    if (!exists) {
                        project.addHashtag(hashtagName);
                    }
                }
            }
        } else if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {
            for (String hashtagName : request.getHashtags()) {
                project.addHashtag(hashtagName);
            }
        }

        Project savedProject = projectRepository.save(project);

        // ProjectMember 생성
        ProjectMemberId projectMemberId = ProjectMemberId.builder()
                .projectId(savedProject.getId())
                .memberId(member.getId())
                .build();

        Optional<ProjectMember> optionalProjectMember = projectMemberRepository.findById(projectMemberId);
        if (optionalProjectMember.isEmpty()) {
            savedProject.addProjectMember(
                    member,
                    LEADER,
                    false,
                    null,
                    false
            );

            // Project를 다시 저장하여 ProjectMember도 함께 저장
            projectRepository.save(savedProject);
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
