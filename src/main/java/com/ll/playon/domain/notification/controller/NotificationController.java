package com.ll.playon.domain.notification.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.notification.dto.request.NotificationRequest;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.service.NotificationService;
import com.ll.playon.domain.notification.service.NotificationSseService;
import com.ll.playon.global.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;
    private final UserContext userContext;
    /**
     * SSE 구독 (알림 실시간 수신)
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        Member actor = this.userContext.getActor();
        return notificationSseService.subscribe(actor.getId());
    }

    /**
     * 알림 전송
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        Member actor = this.userContext.getActor();
        NotificationResponse response = notificationService.sendNotification(request.withSenderId(actor.getId()));
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 알림 읽음 처리
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        Member actor = this.userContext.getActor();
        notificationService.markAsRead(actor.getId(), notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자의 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        Member actor = this.userContext.getActor();
        List<NotificationResponse> notifications = notificationService.getNotifications(actor.getId());
        return ResponseEntity.ok(notifications);
    }
}
