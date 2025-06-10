package Idea.Idea_Hive.notification.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long projectId,
        String message,
        LocalDateTime createdAt
) {
}
