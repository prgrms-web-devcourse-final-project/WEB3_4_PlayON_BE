package com.ll.playon.domain.notification.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.notification.dto.request.NotificationRequest;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.dto.response.NotificationSummaryResponse;
import com.ll.playon.domain.notification.service.NotificationService;
import com.ll.playon.domain.notification.service.NotificationSseService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public RsData<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        Member actor = this.userContext.getActor();
        NotificationResponse response = notificationService.sendNotification(actor.getId(), request);
        return RsData.success(HttpStatus.OK, response);
    }

    /**
     * 특정 알림 읽음 처리
     */
    @PatchMapping("/{notificationId}/read")
    public RsData<String> markAsRead(@PathVariable Long notificationId) {
        Member actor = this.userContext.getActor();
        notificationService.markAsRead(actor.getId(), notificationId);
        return RsData.success(HttpStatus.OK, "알림 읽음 처리 완료");
    }

    /**
     * 사용자의 알림 목록 조회
     */
    @GetMapping
    public RsData<List<NotificationResponse>> getNotifications() {
        Member actor = this.userContext.getActor();
        List<NotificationResponse> notifications = notificationService.getNotifications(actor.getId());
        return RsData.success(HttpStatus.OK, notifications);
    }

    /**
     * 알림 요약 정보 조회
     */
    @GetMapping("/summary")
    public RsData<NotificationSummaryResponse> getNotificationSummary() {
        Member actor = this.userContext.getActor();
        NotificationSummaryResponse summary = notificationService.getNotificationSummary(actor.getId());
        return RsData.success(HttpStatus.OK, summary);
    }
}
