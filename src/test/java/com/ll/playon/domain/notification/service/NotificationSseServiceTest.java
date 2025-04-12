package com.ll.playon.domain.notification.service;

import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.entity.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NotificationSseServiceTest {

    @InjectMocks
    private NotificationSseService notificationSseService;

    private final Long receiverId = 2L;

    @BeforeEach
    void setUp() {
        notificationSseService = new NotificationSseService();
    }

    /**
     * SSE 구독 테스트
     */
    @Test
    void SSE_구독_테스트() {
        // When
        SseEmitter emitter = notificationSseService.subscribe(receiverId);

        // Then
        assertThat(emitter).isNotNull();
    }

    /**
     * SSE 알림 전송 테스트
     */
    @Test
    void SSE_알림_전송_테스트() throws IOException {
        // Given
        SseEmitter emitter = notificationSseService.subscribe(receiverId);

        NotificationResponse response = new NotificationResponse(
                1L,
                "홍길동",
                "SSE 알림 테스트",
                NotificationType.PARTY_INVITE,
                false,
                "https://example.com",
                LocalDateTime.now()
        );



        // When
        notificationSseService.sendSseNotification(receiverId, response);

        // Then
        assertThat(notificationSseService.subscribe(receiverId)).isNotSameAs(emitter);
    }

    /**
     * SSE 구독 해제 테스트
     */
    @Test
    void SSE_구독_해제_테스트() {
        // Given
        SseEmitter emitter = notificationSseService.subscribe(receiverId);

        // When
        emitter.complete(); // 구독 해제

        // Then
        assertThat(notificationSseService.subscribe(receiverId)).isNotSameAs(emitter);
    }
}
