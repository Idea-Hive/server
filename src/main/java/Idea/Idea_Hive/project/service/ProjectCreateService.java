package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.project.dto.request.ProjectCreateRequest;
import Idea.Idea_Hive.project.dto.response.ProjectTempSavedInfoResponse;
import Idea.Idea_Hive.project.entity.*;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.project.entity.repository.SkillStackRepository;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectCreateService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final SkillStackRepository skillStackRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public Long createProject(ProjectCreateRequest request) {
        // 임시저장이 아닌 경우 validation 체크
        if (request.isSave()) {
            validationProjectRequest(request);
        }

        Member member = memberRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        //임시 저장 되어있던 프로젝트를 수정하여 저장하는 경우
        Project project;
        ProjectDetail projectDetail;

        if (request.projectId() != null) { //임시 저장했던 프로젝트가 있는 경우
            project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

            // 프로젝트 정보 업데이트
            project.updateTemporaryProject(
                    request.title(),
                    request.description(),
                    request.contact(),
                    request.maxMembers(),
                    request.dueDateFrom(),
                    request.dueDateTo(),
                    request.isSave()
            );

            projectDetail = project.getProjectDetail();
            projectDetail.updateIdea(request.idea());
        } else { //새로 생성하는 경우
            projectDetail = ProjectDetail.builder()
                    .idea(request.idea())
                    .build();

            project = Project.builder()
                    .title(request.title())
                    .description(request.description())
                    .contact(request.contact())
                    .maxMembers(request.maxMembers())
                    .dueDateFrom(request.dueDateFrom())
                    .dueDateTo(request.dueDateTo())
                    .isSave(request.isSave())
                    .build();
        }

        // 연관관계 설정
        project.setProjectDetail(projectDetail);

        // 기술스택 추가
        if (request.projectId() != null) { //기존에 저장되어있는 프로젝트가 있을 경우
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
        } else if (request.skillStackIds() != null && !request.skillStackIds().isEmpty()) { //새 프로젝트 생성 시 기술스택 추가

            List<SkillStack> skillStacks = skillStackRepository.findAllById(request.skillStackIds());
            if (skillStacks.size() != request.skillStackIds().size()) {
                throw new IllegalArgumentException("존재하지 않는 기술스택이 포함되어 있습니다.");
            }

            for (SkillStack skillStack : skillStacks) {
                project.addSkillStack(skillStack);
            }
        }

        //해시태그 추가
        // 기존에 저장되어있는 프로젝트가 있을 경우
        if (request.projectId() != null) {
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
        } else if (request.hashtags() != null && !request.hashtags().isEmpty()) {
            for (String hashtagName : request.hashtags()) {
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
                    Role.LEADER,
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

    @Transactional(readOnly = true)
    public ProjectTempSavedInfoResponse getTempSavedProjectInfo(Long projectId) {
        ProjectTempSavedInfoResponse info = projectRepository.findTempSavedProjectInfoById(projectId);
        return info;
    }

}
