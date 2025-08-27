package Idea.Idea_Hive.notification.controller;

import Idea.Idea_Hive.notification.dto.NotificationDto;
import Idea.Idea_Hive.notification.entity.Notification;
import Idea.Idea_Hive.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회")
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestParam(required = true) Long userId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "3") int size)
    {
        return ResponseEntity.ok(notificationService.getNotifications(userId, page, size));
    }

    @Operation(summary = "읽지 않은 알림 개수 조회")
    @GetMapping("/unread")
    public ResponseEntity<Long> getUnreadCount(@RequestParam(required = true) Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

}
