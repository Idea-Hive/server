package Idea.Idea_Hive.task.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTaskId implements Serializable {
    private Long projectId;
    private Long taskId;
    private Long taskPathId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())return false;
        ProjectTaskId that = (ProjectTaskId) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(taskId, that.taskId) &&
                Objects.equals(taskPathId, that.taskPathId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, taskId, taskPathId);
    }
}
