package Idea.Idea_Hive.notification.entity.repository;

import Idea.Idea_Hive.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByCreatedDateDesc(Long receiverId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

}
