package com.ll.playon.domain.notification.dto.request;

import com.ll.playon.domain.notification.entity.NotificationType;

public record NotificationRequest(
        Long senderId,
        Long receiverId,
        String content,
        NotificationType type,
        String redirectUrl
) {
}
