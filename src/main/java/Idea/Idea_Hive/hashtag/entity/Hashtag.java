package Idea.Idea_Hive.hashtag.entity;

import Idea.Idea_Hive.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;
    private String name;

    @Builder
    public Hashtag(Project project, String name) {
        this.project = project;
        this.name = name;
    }
}
