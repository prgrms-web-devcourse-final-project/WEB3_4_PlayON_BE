package com.ll.playon.domain.notification.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.entity.NotificationType;
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

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal Member member) {
        return notificationService.subscribe(member.getId());
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@RequestParam Long receiverId,
                                                 @RequestParam String content,
                                                 @RequestParam NotificationType type,
                                                 @RequestParam(required = false) String redirectUrl) {
        notificationService.sendNotification(receiverId, content, type, redirectUrl);
        return ResponseEntity.ok().build();
    }
// TODO: 알림 읽음 처리 API 구현
//    @PatchMapping("/{notificationId}/read")
//    public ResponseEntity<Void> markAsRead(
//            @PathVariable Long notificationId,
//            @AuthenticationPrincipal CustomUserDetails userDetails // 현재 로그인한 사용자 정보 가져오기
//    ) {
//        notificationService.markAsRead(userDetails.getId(), notificationId);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@AuthenticationPrincipal Member member) {
        List<NotificationResponse> notifications = notificationService.getNotifications(member.getId());
        return ResponseEntity.ok(notifications);
    }
}
