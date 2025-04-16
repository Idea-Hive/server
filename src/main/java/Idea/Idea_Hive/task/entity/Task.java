package Idea.Idea_Hive.task.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private Boolean isRequired;

    private String title;
}
