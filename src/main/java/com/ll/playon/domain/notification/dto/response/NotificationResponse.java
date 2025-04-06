package com.ll.playon.domain.notification.dto.response;

import com.ll.playon.domain.notification.entity.Notification;
import com.ll.playon.domain.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long senderId,
        String content,
        NotificationType type,
        boolean isRead,
        String redirectUrl,
        LocalDateTime createdAt
) {
    public static NotificationResponse fromEntity(Notification notification, Long senderId) {
        return new NotificationResponse(
                notification.getId(),
                senderId,
                notification.getContent(),
                notification.getType(),
                notification.isRead(),
                notification.getRedirectUrl(),
                notification.getCreatedAt()
        );
    }

    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                null, // senderId가 없는 경우는 null 처리
                notification.getContent(),
                notification.getType(),
                notification.isRead(),
                notification.getRedirectUrl(),
                notification.getCreatedAt()
        );
    }

}
