package com.ll.playon.domain.notification.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.notification.dto.request.NotificationRequest;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.dto.response.NotificationSummaryResponse;
import com.ll.playon.domain.notification.service.NotificationService;
import com.ll.playon.domain.notification.service.NotificationSseService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "Notification", description = "알림 관련 API")
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
    @Operation(summary = "SSE 구독", description = "실시간 알림 수신을 위한 SSE 연결을 시작합니다.")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        Member actor = this.userContext.getActor();
        return notificationSseService.subscribe(actor.getId());
    }

    /**
     * 알림 전송
     */
    @Operation(summary = "알림 전송", description = "특정 사용자에게 알림을 전송합니다.")
    @PostMapping("/send")
    public RsData<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        Member actor = this.userContext.getActor();
        NotificationResponse response = notificationService.sendNotification(actor.getId(), request);
        return RsData.success(HttpStatus.OK, response);
    }

    /**
     * 특정 알림 읽음 처리
     */
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/{notificationId}/read")
    public RsData<String> markAsRead(@PathVariable Long notificationId) {
        Member actor = this.userContext.getActor();
        notificationService.markAsRead(actor.getId(), notificationId);
        return RsData.success(HttpStatus.OK, "알림 읽음 처리 완료");
    }

    /**
     * 사용자의 알림 목록 조회
     */
    @Operation(summary = "알림 목록 조회", description = "사용자의 전체 알림 목록을 조회합니다.")
    @GetMapping
    public RsData<List<NotificationResponse>> getNotifications() {
        Member actor = this.userContext.getActor();
        List<NotificationResponse> notifications = notificationService.getNotifications(actor.getId());
        return RsData.success(HttpStatus.OK, notifications);
    }

    /**
     * 알림 요약 정보 조회
     */
    @Operation(summary = "알림 요약 조회", description = "최근 10개 알림과 안 읽은 알림 수를 반환합니다.")
    @GetMapping("/summary")
    public RsData<NotificationSummaryResponse> getNotificationSummary() {
        Member actor = this.userContext.getActor();
        NotificationSummaryResponse summary = notificationService.getNotificationSummary(actor.getId());
        return RsData.success(HttpStatus.OK, summary);
    }
}
