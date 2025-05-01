package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.skillstack.entity.SkillStack;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "project_skillStack")
public class ProjectSkillStack {

    @EmbeddedId
    private ProjectSkillStackId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillStackId")
    @JoinColumn(name = "skillStack_id")
    private SkillStack skillStack;

    @Builder
    public ProjectSkillStack(Project project, SkillStack skillStack) {
        this.project = project;
        this.skillStack = skillStack;
        this.id = new ProjectSkillStackId(project.getId(), skillStack.getId());
    }
}
