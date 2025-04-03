package com.ll.playon.domain.notification.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.notification.dto.request.NotificationRequest;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.entity.Notification;
import com.ll.playon.domain.notification.entity.NotificationType;
import com.ll.playon.domain.notification.event.NotificationEvent;
import com.ll.playon.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Member sender;
    private Member receiver;

    @BeforeEach
    void setUp() {
        sender = new Member(1L, "sender@example.com", Role.USER);
        receiver = new Member(2L, "receiver@example.com", Role.USER);
    }

    /**
     * 알림 저장 및 전송 테스트
     */
    @Test
    void 알림_저장_및_전송_테스트() {
        // Given
        NotificationRequest request = new NotificationRequest(
                sender.getId(), // senderId 포함
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

        // receiver만 조회하도록 수정 (sender 조회 삭제)
        when(memberRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        // save() 호출 시 동일한 객체를 반환하도록 설정
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        NotificationResponse response = notificationService.sendNotification(request);

        // Then
        assertThat(response.receiverId()).isEqualTo(receiver.getId());
        assertThat(response.content()).isEqualTo("새로운 파티 초대");

        // 알림이 저장되었는지 검증
        verify(notificationRepository, times(1)).save(any(Notification.class));

        // 이벤트가 발행되었는지 검증
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
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
        // Given
        Notification notification = Notification.builder()
                .receiver(receiver)
                .content("읽음 처리 테스트")
                .type(NotificationType.GUILD_JOIN_REQUEST)
                .redirectUrl("https://example.com")
                .build();

        // ID 강제 설정
        ReflectionTestUtils.setField(notification, "id", 1L);

        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));

        // When
        notificationService.markAsRead(receiver.getId(), 1L);

        // Then
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    void 본인이_아닌_알림_읽음_처리_예외_테스트() {
        // Given
        Notification notification = Notification.builder()
                .receiver(receiver) // receiver는 2L
                .content("읽음 처리 테스트")
                .type(NotificationType.GUILD_JOIN_REQUEST)
                .redirectUrl("https://example.com")
                .build();

        ReflectionTestUtils.setField(notification, "id", 1L);
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));

        // When & Then (다른 사용자가 읽으려 할 때 예외 발생 확인)
        assertThatThrownBy(() -> notificationService.markAsRead(999L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 알림만 읽음 처리할 수 있습니다.");
    }

    @Test
    void 여러_개의_알림_조회_테스트() {
        // Given
        Notification notification1 = Notification.builder()
                .receiver(receiver)
                .content("첫 번째 알림")
                .type(NotificationType.PARTY_INVITE)
                .redirectUrl("https://example.com")
                .build();

        Notification notification2 = Notification.builder()
                .receiver(receiver)
                .content("두 번째 알림")
                .type(NotificationType.GUILD_JOIN_REQUEST)
                .redirectUrl("https://example.com/2")
                .build();

        when(notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiver.getId()))
                .thenReturn(List.of(notification1, notification2));

        // When
        List<NotificationResponse> notifications = notificationService.getNotifications(receiver.getId());

        // Then
        assertThat(notifications).hasSize(2);
        assertThat(notifications.get(0).content()).isEqualTo("첫 번째 알림");
        assertThat(notifications.get(1).content()).isEqualTo("두 번째 알림");
    }

}
