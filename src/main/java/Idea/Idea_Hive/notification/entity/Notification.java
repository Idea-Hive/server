package Idea.Idea_Hive.notification.entity;

import Idea.Idea_Hive.member.entity.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    private String message;

    private LocalDateTime createdDate;

    private boolean isRead;

    @Builder
    public Notification(Member receiver, String message) {
        this.receiver = receiver;
        this.message = message;
        this.createdDate = LocalDateTime.now();
        this.isRead = false;
    }

}
