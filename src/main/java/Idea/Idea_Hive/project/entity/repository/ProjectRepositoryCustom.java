package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.project.entity.Project;

import java.util.List;

public interface ProjectRepositoryCustom {
    List<Project> searchByTitleAndHashtag(String keyword, String recruitType, Long hashtagId);
}
