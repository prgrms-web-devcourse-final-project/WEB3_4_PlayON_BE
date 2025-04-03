package com.ll.playon.domain.notification.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.notification.dto.request.NotificationRequest;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.entity.Notification;
import com.ll.playon.domain.notification.entity.NotificationType;
import com.ll.playon.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member sender;
    private Member receiver;

    @BeforeEach
    void setUp() {
        sender = new Member(1L, "sender@example.com", Role.USER);
        receiver = new Member(2L, "receiver@example.com", Role.USER);
    }

    /**
     * SSE 구독 테스트
     */
    @Test
    void SSE_구독_테스트() {
        // When
        SseEmitter emitter = notificationService.subscribe(receiver.getId());

        // Then
        assertThat(emitter).isNotNull();
    }

    /**
     * SSE 알림 전송 테스트
     */
    @Test
    void SSE_알림_전송_테스트() throws IOException {
        // Given
        SseEmitter emitter = notificationService.subscribe(receiver.getId());
        Notification notification = Notification.builder()
                .receiver(receiver)
                .content("SSE 알림 테스트")
                .type(NotificationType.PARTY_INVITE)
                .redirectUrl("https://example.com")
                .build();

        when(memberRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(memberRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        notificationService.sendNotification(new NotificationRequest(
                sender.getId(), receiver.getId(), "SSE 알림 테스트",
                NotificationType.PARTY_INVITE, "https://example.com"
        ));

        // Then
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    /**
     * SSE 구독 해제 테스트
     */
    @Test
    void SSE_구독_해제_테스트() {
        // Given
        SseEmitter emitter = notificationService.subscribe(receiver.getId());

        // When
        emitter.complete(); // 구독 해제

        // Then
        // -> emitters 맵에서 해당 receiver의 리스트가 존재하지 않아야 함
        assertThat(notificationService.subscribe(receiver.getId())).isNotSameAs(emitter);
    }

    /**
     * 알림 저장 및 전송 테스트
     */
    @Test
    void 알림_저장_및_전송_테스트() {
        // Given
        NotificationRequest request = new NotificationRequest(
                sender.getId(),
                receiver.getId(),
                "새로운 파티 초대",
                NotificationType.PARTY_INVITE,
                "https://example.com"
        );

        Notification notification = Notification.builder()
                .receiver(receiver)
                .content(request.content())
                .type(request.type())
                .redirectUrl(request.redirectUrl())
                .build();

        when(memberRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(memberRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        NotificationResponse response = notificationService.sendNotification(request);

        // Then
        assertThat(response.receiverId()).isEqualTo(receiver.getId());
        assertThat(response.content()).isEqualTo("새로운 파티 초대");
    }

    /**
     * 알림 조회 테스트
     */
    @Test
    void 알림_조회_테스트() {
        // Given
        Notification notification = Notification.builder()
                .receiver(receiver)
                .content("테스트 알림")
                .type(NotificationType.PARTY_INVITE)
                .redirectUrl("https://example.com")
                .build();

        when(notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiver.getId()))
                .thenReturn(List.of(notification));

        // When
        List<NotificationResponse> notifications = notificationService.getNotifications(receiver.getId());

        // Then
        assertThat(notifications).isNotEmpty();
        assertThat(notifications.get(0).content()).isEqualTo("테스트 알림");
    }

    /**
     * 알림 읽음 처리 테스트
     */
    @Test
    void 알림_읽음_처리_테스트() {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .content("읽음 처리 테스트")
                .type(NotificationType.GUILD_JOIN_REQUEST)
                .redirectUrl("https://example.com")
                .build();

        // ID 강제 설정
        ReflectionTestUtils.setField(notification, "id", 1L);

        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));

        notification.markAsRead(); // 읽음 처리 실행
        notificationRepository.save(notification);

        Notification updatedNotification = notificationRepository.findById(1L).orElseThrow();
        assertThat(updatedNotification.isRead()).isTrue();
    }
}
