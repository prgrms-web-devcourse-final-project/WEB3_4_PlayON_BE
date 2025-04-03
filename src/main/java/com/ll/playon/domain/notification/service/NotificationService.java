package com.ll.playon.domain.notification.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.notification.dto.request.NotificationRequest;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.entity.Notification;
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

    // 여러 개의 SSE 구독을 허용하는 구조
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * SSE 구독 메서드 (알림 수신을 위한 연결)
     */
    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(memberId, key -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(memberId, emitter));
        emitter.onTimeout(() -> removeEmitter(memberId, emitter));

        // 구독 직후 더미 이벤트 전송 (연결 유지)
        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected!"));
        } catch (IOException e) {
            removeEmitter(memberId, emitter);
        }

        return emitter;
    }

    private void removeEmitter(Long memberId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(memberId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(memberId);
            }
        }
    }

    /**
     * 알림 전송 (DB 저장 + SSE 실시간 전송)
     */
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        Member sender = memberRepository.findById(request.senderId())
                .orElseThrow(() -> new EntityNotFoundException("발신자를 찾을 수 없습니다: " + request.senderId()));

        Member receiver = memberRepository.findById(request.receiverId())
                .orElseThrow(() -> new EntityNotFoundException("수신자를 찾을 수 없습니다: " + request.receiverId()));

        Notification notification = Notification.builder()
                .receiver(receiver)
                .content(request.content())
                .type(request.type())
                .redirectUrl(request.redirectUrl())
                .build();
        notificationRepository.save(notification);

        sendSseNotification(receiver.getId(), NotificationResponse.fromEntity(notification));

        return NotificationResponse.fromEntity(notification);
    }

    private void sendSseNotification(Long receiverId, NotificationResponse response) {
        List<SseEmitter> userEmitters = emitters.get(receiverId);
        if (userEmitters != null) {
            userEmitters.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(response));
                    return false;
                } catch (IOException e) {
                    return true;
                }
            });
            if (userEmitters.isEmpty()) {
                emitters.remove(receiverId);
            }
        }
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiver().getId().equals(memberId)) {
            throw new IllegalArgumentException("잘못된 요청: 본인의 알림만 읽음 처리할 수 있습니다.");
        }

        notification.markAsRead();
    }

    /**
     * 사용자의 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long memberId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

}
