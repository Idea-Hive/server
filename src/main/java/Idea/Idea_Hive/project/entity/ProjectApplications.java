package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProjectApplications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String applicationMessage;

    @Enumerated(EnumType.STRING)
    private IsAccepted isAccepted;

    private LocalDateTime applicationDate;

    private String rejectionMessage;

    private Boolean isReApplication;

    public void updateIsAccepted(IsAccepted isAccepted) {
        this.isAccepted = isAccepted;
    }

    public void updateRejectionMessage(String rejectionMessage) {
        this.rejectionMessage = rejectionMessage;
    }

    public void updateApplicationMessage(String applicationMessage) {
        this.applicationMessage = applicationMessage;
    }

}
