package Idea.Idea_Hive.project.entity;

import jakarta.persistence.*;

@Entity
public class ProjectDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "projectDetail")
    private Project project;

    @Lob
    private String idea;
}
