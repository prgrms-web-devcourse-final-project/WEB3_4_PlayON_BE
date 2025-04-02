package com.ll.playon.domain.notification.dto;

import com.ll.playon.domain.notification.entity.Notification;
import com.ll.playon.domain.notification.entity.NotificationType;

public record NotificationResponse(
        Long id,
        String content,
        NotificationType type,
        boolean read,
        String redirectUrl
) {
    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getContent(),
                notification.getType(),
                notification.isRead(),
                notification.getRedirectUrl()
        );
    }
}
