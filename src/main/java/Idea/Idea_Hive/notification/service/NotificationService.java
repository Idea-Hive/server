package Idea.Idea_Hive.notification.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.notification.dto.NotificationDto;
import Idea.Idea_Hive.project.entity.Role;
import Idea.Idea_Hive.project.entity.repository.ProjectMemberRepository;
import Idea.Idea_Hive.notification.entity.Notification;
import Idea.Idea_Hive.notification.entity.repository.NotificationRepository;
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

    public void sendProjectApplicationNotification(Long projectId, String message) {

        //프로젝트 생성자 ID 조회
        Long creatorId = projectMemberRepository.findByProjectIdAndRole(projectId, Role.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트 리더를 찾을 수 없습니다."))
                .getMember().getId();

        //데이터베이스에 저장
        Member receiver = memberRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Notification notification = Notification.builder()
                .receiver(receiver)
                .message(message)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // DTO 생성
        NotificationDto notificationDto = new NotificationDto(
                savedNotification.getId(),
                savedNotification.getMessage(),
                savedNotification.getCreatedDate()
        );

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
                        notification.getMessage(),
                        notification.getCreatedDate()
                ))
                .toList();
    }

    //읽지 않은 알림 개수 조회
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }
}
