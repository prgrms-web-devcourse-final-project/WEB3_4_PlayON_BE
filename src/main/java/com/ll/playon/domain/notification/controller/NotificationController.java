package com.ll.playon.domain.notification.controller;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
                                                 @RequestParam String message) {
        notificationService.sendNotification(receiverId, message);
        return ResponseEntity.ok().build();
    }
}
