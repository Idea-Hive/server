package Idea.Idea_Hive.project.service;

import Idea.Idea_Hive.project.dto.response.ProjectSearchResponse;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import Idea.Idea_Hive.project.entity.repository.manage.ProjectManageRepository;
import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.repository.TaskRepository;
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
public class ProjectManageService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final ProjectManageRepository projectManageRepository;

    @Transactional
    public void submit(Long projectId) {
        // todo: Project 불러오기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        // todo: 제출에 필요한 조건 확인 후 예외처리하기
        /**
         * 1. 제출 권한 확인
         * 2. 과제 완료 했는지 확인(?) <-- 필수인지 검토 후 추가..
         * 3. 기타 제출 조건 확인
         */

        List<Task> tasks = taskRepository.findTasksByProjectId(projectId)
                .stream().filter(Task::getIsRequired).toList(); // 필수 제출만
        for (Task task : tasks) {
            if (!task.getIsSubmitted()) { // 제출 안한 경우 예외 던짐
                throw new IllegalArgumentException("제출하지 않은 항목이 있습니다 : " + task.getTitle());
            }
        }
        // todo: 상태 업데이트하기
        project.updateStatus(ProjectStatus.COMPLETED);
    }

    public ProjectSearchResponse getProjectListByStatus(Long memberId, ProjectStatus status, Pageable pageable) {
        Page<Project> projects = projectManageRepository.findProjectByMemberIdAndStatusWithPage(memberId, status, pageable);
        return ProjectSearchResponse.of(projects);
    }
}
