package com.ll.playon.domain.notification.entity;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @Column(length = 255)
    private String redirectUrl;

    // 읽음 상태 변경 메서드
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
        }
    }

    // 팩토리 메서드
    public static Notification create(Member receiver,Member sender , String content, NotificationType type, String redirectUrl) {
        return Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .content(content)
                .type(type)
                .redirectUrl(redirectUrl)
                .build();
    }
}