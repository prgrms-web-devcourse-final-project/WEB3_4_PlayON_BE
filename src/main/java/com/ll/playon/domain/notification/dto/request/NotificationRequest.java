package com.ll.playon.domain.notification.dto.request;

import com.ll.playon.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "알림 요청 정보")
public record NotificationRequest(
        @Schema(description = "수신자 ID", example = "2") @NotNull
        Long receiverId,
        @Schema(description = "알림 유형", example = "PARTY_INVITE") @NotNull
        NotificationType type,

        @Schema(description = "알림 내용", example = "초대장이 도착했습니다.", nullable = true)
        String content,

        @Schema(description = "리디렉션 URL (알림 클릭 시 이동)", example = "/party/3", nullable = true)
        String redirectUrl
) {}
