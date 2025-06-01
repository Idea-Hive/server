package Idea.Idea_Hive.task.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "project_task")
public class ProjectTask {

    @EmbeddedId
    private ProjectTaskId id;


}
