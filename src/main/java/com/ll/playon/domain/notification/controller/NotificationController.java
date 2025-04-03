package com.ll.playon.domain.notification.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.notification.dto.request.NotificationRequest;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * SSE 구독 (알림 실시간 수신)
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal Member member) {
        return notificationService.subscribe(member.getId());
    }

    /**
     * 알림 전송
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @RequestBody NotificationRequest request,
            @AuthenticationPrincipal Member sender
    ) {
        NotificationResponse response = notificationService.sendNotification(request.withSenderId(sender.getId()));
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 알림 읽음 처리
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal Member member
    ) {
        notificationService.markAsRead(member.getId(), notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자의 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@AuthenticationPrincipal Member member) {
        List<NotificationResponse> notifications = notificationService.getNotifications(member.getId());
        return ResponseEntity.ok(notifications);
    }
}
