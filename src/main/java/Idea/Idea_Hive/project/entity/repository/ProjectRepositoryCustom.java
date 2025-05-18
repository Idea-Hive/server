package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectRepositoryCustom {
    Page<Project> searchByKeyword(String keyword, String recruitType, String sortType, Pageable pageable);
    ProjectInfoResponse findProjectInfoById(Long projectId);
}
