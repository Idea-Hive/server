package Idea.Idea_Hive.project.entity.repository.manage;

import Idea.Idea_Hive.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 충돌 방지로 따로 만들어놨습니다.
 * 추 후 개발 완료 시 기존 ProjectRepository에 합칠 예정입니다.
 */
@Repository
public interface ProjectManageRepository extends JpaRepository<Project, Long>, ProjectManageRepositoryCustom {
}
