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
    @Setter
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Setter
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

    public void setProject(Project project) {

        // 기존에 참고하고 있던 project가 있다 ? -> 삭제
        if (this.project != null) {
            this.project.getProjectMembers().remove(this);
        }

        this.project = project;

        if (project != null && !project.getProjectMembers().contains(this)) {
            project.getProjectMembers().add(this);
        }

    }

}
