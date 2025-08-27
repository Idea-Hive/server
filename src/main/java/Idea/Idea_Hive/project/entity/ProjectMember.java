package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Setter
    private Role role;

    private boolean isProfileShared;

    private LocalDateTime profileSharedDate;

    private boolean isLike;

    public void updateLike(boolean isLike) {
        this.isLike = isLike;
    }

    public void updateProfileShared(boolean isProfileShared) {
        this.isProfileShared = isProfileShared;
        this.profileSharedDate = LocalDateTime.now();
    }

    public void updateRole(Role role) {
        this.role = role;
    }

}
