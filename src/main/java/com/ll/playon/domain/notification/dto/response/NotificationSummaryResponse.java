package com.ll.playon.domain.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "알림 요약 정보")
public record NotificationSummaryResponse(

        @Schema(description = "최근 알림 목록 (최대 10개)")
        List<NotificationResponse> notifications,

        @Schema(description = "읽지 않은 알림 수", example = "3")
        long unreadCount
) {
    public static NotificationSummaryResponse of(List<NotificationResponse> notifications, long unreadCount) {
        return new NotificationSummaryResponse(notifications, unreadCount);
    }
}