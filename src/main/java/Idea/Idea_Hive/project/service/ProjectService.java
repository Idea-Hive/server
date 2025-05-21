package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import Idea.Idea_Hive.project.dto.request.ProjectCreateRequest;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.dto.response.ProjectTempSavedResponse;
import Idea.Idea_Hive.project.entity.*;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.project.entity.repository.SkillStackRepository;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.task.entity.QProjectTaskId;
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

    @Transactional
    public ProjectInfoResponse getProjectInfo(Long projectId) {
        projectRepository.increaseViewCnt(projectId);
        ProjectInfoResponse projectInfoResponse = projectRepository.findProjectInfoById(projectId);
        return projectInfoResponse;
    }

}

