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

    @EmbeddedId
    private ProjectMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    private String applicationMessage;

    @Enumerated(EnumType.STRING)
    private IsAccepted isAccepted;

    private LocalDateTime applicationDate;

    private String rejectionMessage;

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
