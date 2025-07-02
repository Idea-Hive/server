package Idea.Idea_Hive.notification.entity;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private Long projectId;

    private Long receiverId;

    private Long applicationId;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private LocalDateTime createdDate;

    private boolean isRead;

    @Builder
    public Notification(Long projectId, Long receiverId, Long applicationId, NotificationType notificationType) {
        this.projectId = projectId;
        this.receiverId = receiverId;
        this.applicationId = applicationId;
        this.notificationType = notificationType;
        this.createdDate = LocalDateTime.now();
        this.isRead = false;
    }

}
