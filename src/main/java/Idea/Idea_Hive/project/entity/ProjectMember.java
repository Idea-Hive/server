package Idea.Idea_Hive.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
