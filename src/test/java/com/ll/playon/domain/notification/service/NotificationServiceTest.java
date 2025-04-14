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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        Long senderId = sender.getId();

        NotificationRequest request = new NotificationRequest(
                receiver.getId(),
                NotificationType.PARTY_INVITE,
                "새로운 파티 초대",
                "https://example.com"
        );

        Notification notification = Notification.builder()
                .receiver(receiver)
                .content(request.content())
                .type(request.type())
                .redirectUrl(request.redirectUrl())
                .build();

        // sender, receiver 모두 조회 가능하도록 설정
        when(memberRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(memberRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        NotificationResponse response = notificationService.sendNotification(senderId, request);

        // Then
        assertThat(response.content()).isEqualTo("새로운 파티 초대");
        assertThat(response.type()).isEqualTo(NotificationType.PARTY_INVITE);
        assertThat(response.redirectUrl()).isEqualTo("https://example.com");
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
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

}
