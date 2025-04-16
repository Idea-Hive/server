package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.member.entity.MemberHashtagId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProjectHashtagId implements Serializable {
    private Long projectId;
    private Long hashtagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())return false;
        ProjectHashtagId that = (ProjectHashtagId) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(hashtagId, that.hashtagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, hashtagId);
    }
}
