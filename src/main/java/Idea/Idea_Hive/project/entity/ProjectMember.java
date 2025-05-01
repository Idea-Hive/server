package Idea.Idea_Hive.project.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    private boolean isProfileShared;

    private LocalDateTime profileSharedDate;

    private boolean isFavorited;

}
