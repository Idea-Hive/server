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
    private Long skillstackId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())return false;
        ProjectSkillStackId that = (ProjectSkillStackId) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(skillstackId, that.skillstackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, skillstackId);
    }
}
