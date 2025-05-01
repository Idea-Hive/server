package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.project.entity.Project;

import java.util.List;

public interface ProjectRepositoryCustom {
    List<Project> searchByKeyword(String keyword, String recruitType);
}
