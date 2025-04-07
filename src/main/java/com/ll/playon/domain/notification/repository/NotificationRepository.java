package com.ll.playon.domain.notification.repository;

import com.ll.playon.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long memberId);

    // 요약용 : 최근 10개만 가져오기
    List<Notification> findTop10ByReceiverIdOrderByCreatedAtDesc(Long memberId);

    // 요약용 : 안 읽은 알림 수 카운트
    long countByReceiverIdAndReadFalse(Long receiverId);
}
