package Idea.Idea_Hive.project.service;


import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponse;
import Idea.Idea_Hive.project.dto.response.ProjectApplicantResponseDto;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.entity.ProjectApplications;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ProjectApplicantResponse getApplicantInfo(Long projectId, Pageable pageable) {
        Page<ProjectApplications> projectApplicantPage = projectRepository.findApplicantInfoById(projectId, pageable);
        return ProjectApplicantResponse.of(projectApplicantPage, projectRepository);
    }

}

