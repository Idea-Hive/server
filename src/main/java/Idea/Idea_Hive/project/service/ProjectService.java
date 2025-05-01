package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.project.dto.response.ProjectResponseDto;
import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectSearchResponse searchProjects(String keyword, String recruitType) {
        List<Project> projects = projectRepository.searchByKeyword(keyword, recruitType);
        return ProjectSearchResponse.of(projects);
    }
}
