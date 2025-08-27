package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.skillstack.entity.SkillStack;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "project_skillstack")
public class ProjectSkillStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skillstack_id")
    private SkillStack skillstack;

    @Builder
    public ProjectSkillStack(Project project, SkillStack skillstack) {
        this.project = project;
        this.skillstack = skillstack;
    }
}
