package com.ll.playon.domain.title.entity;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberStat extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    @Builder.Default
    private int statValue = 0;

    public MemberStat addStat() {
        this.statValue++;
        return this;
    }
}
