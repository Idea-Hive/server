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

    public void updateIsAcceptedAndRejectMessage(IsAccepted isAccepted, String rejectionMessage) {
        this.isAccepted = isAccepted;
        this.rejectionMessage = rejectionMessage;
    }
}
