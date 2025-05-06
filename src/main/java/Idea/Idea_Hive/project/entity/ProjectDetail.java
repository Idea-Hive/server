package Idea.Idea_Hive.project.entity;

import jakarta.persistence.*;
import lombok.Builder;

@Entity
public class ProjectDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Lob
    private String idea;

    public void updateIdea(String idea) {
        this.idea = idea;
    }

    @Builder
    public ProjectDetail(String idea) {
        this.idea = idea;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
