package Idea.Idea_Hive.task.entity;

import Idea.Idea_Hive.project.entity.Project;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "project_task")
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Builder
    public ProjectTask(Project project, Task task) {
        this.project = project;
        this.task = task;
    }

    public void setProject(Project project) {
        // 기존에 참고하고 있던 project가 있다 ? -> 삭제
        if (this.project != null) {
            this.project.getProjectTasks().remove(this);
        }

        this.project = project;

        if (project != null && !project.getProjectTasks().contains(this)) {
            project.getProjectTasks().add(this);
        }
    }
}
