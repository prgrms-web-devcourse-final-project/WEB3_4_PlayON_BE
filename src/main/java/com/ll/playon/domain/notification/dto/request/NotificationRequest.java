package com.ll.playon.domain.notification.dto.request;

import com.ll.playon.domain.notification.entity.NotificationType;

public record NotificationRequest(
        Long senderId,
        Long receiverId,
        String content,
        NotificationType type,
        String redirectUrl
) {
    public NotificationRequest withSenderId(Long senderId) {
        return new NotificationRequest(senderId, receiverId, content, type, redirectUrl);
    }
}
