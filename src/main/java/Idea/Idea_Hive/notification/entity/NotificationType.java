package Idea.Idea_Hive.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    PROJECT_APPLICATION("프로젝트 지원"),
    PROJECT_APPLICATION_REJECTED("프로젝트 지원 거절"),
    PROJECT_APPLICATION_ACCEPTED("프로젝트 지원 확정"),
    PROJECT_CONFIRMATION_CANCELLED("프로젝트 확정 취소");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }
}
