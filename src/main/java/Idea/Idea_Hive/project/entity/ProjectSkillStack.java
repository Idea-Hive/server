package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.skillstack.entity.SkillStack;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "project_skillstack")
public class ProjectSkillStack {

    @EmbeddedId
    private ProjectSkillStackId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillstackId")
    @JoinColumn(name = "skillstack_id")
    private SkillStack skillstack;

    @Builder
    public ProjectSkillStack(Project project, SkillStack skillstack) {
        this.project = project;
        this.skillstack = skillstack;
        this.id = new ProjectSkillStackId(project.getId(), skillstack.getId());
    }
}
