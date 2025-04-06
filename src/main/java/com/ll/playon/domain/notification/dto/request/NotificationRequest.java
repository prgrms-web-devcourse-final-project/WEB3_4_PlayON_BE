package com.ll.playon.domain.notification.dto.request;

import com.ll.playon.domain.notification.entity.NotificationType;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotNull Long receiverId,
        @NotNull NotificationType type,
        String content,
        String redirectUrl
) {}
