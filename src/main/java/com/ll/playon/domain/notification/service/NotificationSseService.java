package com.ll.playon.domain.notification.service;

import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class NotificationSseService {

    // 여러 개의 SSE 구독을 허용하는 구조
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * SSE 구독 (알림 수신을 위한 연결)
     */
    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(memberId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(memberId, emitter));
        emitter.onTimeout(() -> removeEmitter(memberId, emitter));

        sendDummyEvent(emitter);

        return emitter;
    }

    /**
     * SSE 실시간 알림 전송
     */
    public void sendSseNotification(Long receiverId, NotificationResponse response) {
        List<SseEmitter> userEmitters = emitters.get(receiverId);
        if (userEmitters == null) return;

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

    private void sendDummyEvent(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected!"));
        } catch (IOException e) {
            emitter.complete();
        }
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
}
