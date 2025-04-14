package com.ll.playon.domain.notification.service;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.notification.dto.request.NotificationRequest;
import com.ll.playon.domain.notification.dto.response.NotificationResponse;
import com.ll.playon.domain.notification.dto.response.NotificationSummaryResponse;
import com.ll.playon.domain.notification.entity.Notification;
import com.ll.playon.domain.notification.entity.NotificationType;
import com.ll.playon.domain.notification.event.NotificationEvent;
import com.ll.playon.domain.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 알림 전송 (DB 저장 후 이벤트 발행)
     */
    @Transactional
    public NotificationResponse sendNotification(Long senderId, NotificationRequest request) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("발신자를 찾을 수 없습니다: " + senderId));

        Member receiver = memberRepository.findById(request.receiverId())
                .orElseThrow(() -> new EntityNotFoundException("수신자를 찾을 수 없습니다: " + request.receiverId()));

        NotificationType type = request.type();
        String content = request.content() != null ? request.content() : type.getDefaultMessage();
        String redirectUrl = request.redirectUrl() != null ? request.redirectUrl() : type.getDefaultRedirectUrl();

        Notification notification = Notification.create(receiver, content, type, redirectUrl);
        notificationRepository.save(notification);

        String senderNickname = sender != null ? sender.getNickname() : null;

        NotificationResponse response = NotificationResponse.fromEntity(notification, senderNickname);
        eventPublisher.publishEvent(new NotificationEvent(this, receiver.getId(), response));


        return response;
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
        Member sender = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + memberId));

        String senderNickname = sender.getNickname();  // 발신자 닉네임 가져오기

        return notificationRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(memberId)
                .stream()
                .map(notification -> NotificationResponse.fromEntity(notification, senderNickname))  // senderNickname 전달
                .toList();
    }


    /**
     * 사용자의 알림 요약 정보 조회 (최신 10개 알림 및 읽지 않은 알림 개수)
     */
    @Transactional(readOnly = true)
    public NotificationSummaryResponse getNotificationSummary(Long memberId) {
        // 최신 10개 알림 조회
        List<Notification> latest = notificationRepository.findTop10ByReceiverIdOrderByCreatedAtDesc(memberId);

        // 읽지 않은 알림 개수 조회
        long unreadCount = notificationRepository.countByReceiverIdAndIsReadFalse(memberId);

        // 회원의 닉네임 가져오기 (예시로 memberRepository를 통해 회원 정보 조회)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + memberId));
        String senderNickname = member.getNickname();  // 발신자 닉네임

        // NotificationSummaryResponse 생성 시 senderNickname을 전달
        return NotificationSummaryResponse.of(latest, unreadCount, senderNickname);
    }


}
