package Idea.Idea_Hive.task.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "project_task")
public class ProjectTask {

    @Setter
    @EmbeddedId
    private ProjectTaskId id;



}
