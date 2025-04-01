package com.ll.playon.domain.notification.dto;

import com.ll.playon.domain.notification.entity.Notification;
import com.ll.playon.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String content;
    private NotificationType type;
    private boolean read;
    private String redirectUrl;

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
