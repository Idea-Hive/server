package Idea.Idea_Hive.notification.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String message,
        LocalDateTime createdAt
) {
}
