package Idea.Idea_Hive.notification.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.notification.dto.NotificationDto;
import Idea.Idea_Hive.notification.entity.NotificationType;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.Role;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.notification.entity.Notification;
import Idea.Idea_Hive.notification.entity.repository.NotificationRepository;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

//    private final SimpMessagingTemplate messagingTemplate;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final ProjectRepository projectRepository;

    public void addNotification(Long projectId, Long applicationId, NotificationType notificationType) {

        //프로젝트 생성자 ID 조회
        Long creatorId = projectMemberRepository.findByProjectIdAndRole(projectId, Role.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트 리더를 찾을 수 없습니다."))
                .getMember().getId();

        Long receiverId = null;

        if (notificationType != NotificationType.PROJECT_APPLICATION) {
            receiverId = applicationId;
        } else {
            receiverId = creatorId;
        }
        Notification notification = Notification.builder()
                .projectId(projectId)
                .receiverId(receiverId)
                .applicationId(applicationId)
                .notificationType(notificationType)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // 실시간 알림이 필요한 경우에만 DTO 생성
//        NotificationDto notificationDto = new NotificationDto(
//                savedNotification.getId(),
//                savedNotification.getMessage(),
//                savedNotification.getCreatedDate()
//        );

//        //실시간 알림 전송(현재 접속 중인 경우)
//        messagingTemplate.convertAndSend(
//                "/topic/user/" + creatorId,
//                notificationDto
//        );

    }

    //알림 목록 조회
    public List<NotificationDto> getNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(0, page * size);
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedDateDesc(userId,pageable);

        return notifications.stream()
                .map(notification -> new NotificationDto(
                        notification.getId(),
                        generateNotificationMessage(notification),
                        notification.getCreatedDate()
                ))
                .toList();
    }

    //읽지 않은 알림 개수 조회
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    // 알림 메시지 동적 생성
    private String generateNotificationMessage(Notification notification) {
        Project project = projectRepository.findById(notification.getProjectId())
                .orElse(null);
        String projectName = project != null ? project.getName() : "알 수 없는 프로젝트";

        return switch (notification.getNotificationType()) {
            case PROJECT_APPLICATION -> {
                if (notification.getApplicationId() != null) {
                    Member applicant = memberRepository.findById(notification.getApplicationId())
                            .orElse(null);
                    String applicantName = applicant != null ? applicant.getName() : "알 수 없는 지원자";
                    yield String.format("%s님이 \"%s\" 프로젝트에 지원하였습니다.", applicantName, projectName);
                } else {
                    throw new IllegalArgumentException("지원자 정보가 없습니다.");
                }
            }
            case PROJECT_APPLICATION_REJECTED -> String.format("\"%s\" 프로젝트의 담당자가 지원을 수락하지 않았습니다.", projectName);
            case PROJECT_APPLICATION_ACCEPTED -> String.format("\"%s\" 프로젝트 참여에 확정되었습니다.", projectName);
            case PROJECT_CONFIRMATION_CANCELLED -> String.format("\"%s\" 프로젝트에서 참여 확정이 취소되었습니다.", projectName);
            default -> "알림이 도착했습니다.";
        };
    }
}
