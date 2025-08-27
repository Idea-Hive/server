package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.dto.response.ProjectTempSavedResponse;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectSearchService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public ProjectSearchResponse searchProjects(String keyword, String recruitType, String sortType, Pageable pageable) {
        Page<Project> projectPage = projectRepository.searchByKeyword(keyword, recruitType, sortType, pageable);
        return ProjectSearchResponse.of(projectPage);
    }

    @Transactional(readOnly = true)
    public List<ProjectTempSavedResponse> getTempSavedProjects(Long userId) {
        List<Project> projects = projectRepository.findByProjectMembers_MemberIdAndIsSaveFalseOrderByCreatedDateDesc(userId);
        return ProjectTempSavedResponse.from(projects);
    }

}
