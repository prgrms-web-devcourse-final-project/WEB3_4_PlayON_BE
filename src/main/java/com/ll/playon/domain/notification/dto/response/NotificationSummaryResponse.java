package com.ll.playon.domain.notification.dto.response;

import com.ll.playon.domain.notification.entity.Notification;

import java.util.List;

public record NotificationSummaryResponse(
        List<NotificationResponse> notifications,
        long unreadCount
) {
    public static NotificationSummaryResponse of(List<Notification> notifications, long unreadCount) {
        List<NotificationResponse> responses = notifications.stream()
                .map(NotificationResponse::fromEntity)
                .toList();
        return new NotificationSummaryResponse(responses, unreadCount);
    }
}