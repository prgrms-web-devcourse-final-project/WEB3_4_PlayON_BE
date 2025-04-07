package com.ll.playon.domain.title.entity;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberTitle extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_id")
    private Title title;

    @Builder.Default
    private LocalDateTime acquiredAt = LocalDateTime.now();

    @Builder.Default
    private boolean isRepresentative = false;

    public void setRepresentative(boolean representative) {
        this.isRepresentative = representative;
    }
}
