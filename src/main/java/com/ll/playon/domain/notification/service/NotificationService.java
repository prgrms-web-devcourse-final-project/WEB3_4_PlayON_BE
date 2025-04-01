package com.ll.playon.domain.notification.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.notification.dto.NotificationResponse;
import com.ll.playon.domain.notification.entity.Notification;
import com.ll.playon.domain.notification.entity.NotificationType;
import com.ll.playon.domain.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        return emitter;
    }

    @Transactional
    public void sendNotification(Long receiverId, String content, NotificationType type, String redirectUrl) {
        // 1️⃣ receiverId를 기반으로 Member 조회
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + receiverId));

        // 2️⃣ 알림 DB에 저장
        Notification notification = Notification.builder()
                .receiver(receiver)
                .content(content)
                .type(type)
                .redirectUrl(redirectUrl)
                .build();
        notificationRepository.save(notification);

        // 3️⃣ SSE로 실시간 전송
        SseEmitter emitter = emitters.get(receiverId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(content));
            } catch (IOException e) {
                emitters.remove(receiverId);
            }
        }
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));
        notification.markAsRead();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(memberId);
        return notifications.stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }
}
