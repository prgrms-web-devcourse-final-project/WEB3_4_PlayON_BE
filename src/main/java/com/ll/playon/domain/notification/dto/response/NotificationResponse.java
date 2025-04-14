package com.ll.playon.domain.notification.dto.response;

import com.ll.playon.domain.notification.entity.Notification;
import com.ll.playon.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record NotificationResponse(
        @Schema(description = "알림 ID", example = "10")
        Long id,

        @Schema(description = "발신자 닉네임", example = "홍길동")
        String senderNickname,

        @Schema(description = "알림 내용", example = "초대장이 도착했습니다.")
        String content,

        @Schema(description = "알림 유형", example = "PARTY_INVITE")
        NotificationType type,

        @Schema(description = "읽음 여부", example = "false")
        boolean isRead,

        @Schema(description = "리디렉션 URL (알림 클릭 시 이동)", example = "/party/3", nullable = true)
        @Pattern(regexp = "^(https?:\\/\\/\\S+|\\/\\S*)$", message = "URL 형식이 올바르지 않습니다.")
        String redirectUrl,

        @Schema(description = "알림 생성 일시", example = "2024-04-07T12:00:00")
        LocalDateTime createdAt
) {
    public static NotificationResponse fromEntity(Notification notification, String senderNickname) {
        return new NotificationResponse(
                notification.getId(),
                senderNickname,
                notification.getContent(),
                notification.getType(),
                notification.isRead(),
                notification.getRedirectUrl(),
                notification.getCreatedAt()
        );
    }

}
