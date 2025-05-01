package Idea.Idea_Hive.project.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSkillStackId implements Serializable {
    private Long projectId;
    private Long skillStackId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())return false;
        ProjectSkillStackId that = (ProjectSkillStackId) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(skillStackId, that.skillStackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, skillStackId);
    }
}
