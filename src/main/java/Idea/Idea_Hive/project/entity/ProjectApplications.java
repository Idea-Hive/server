package Idea.Idea_Hive.project.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
public class ProjectApplications {

    @EmbeddedId
    private ProjectMemberId id;

    private String applicationMessage;

    private boolean isAccepted;

    private LocalDateTime applicationDate;

    private String rejectionMessage;
}
