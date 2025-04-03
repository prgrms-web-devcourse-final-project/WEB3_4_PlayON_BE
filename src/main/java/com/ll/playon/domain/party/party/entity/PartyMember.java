package com.ll.playon.domain.party.party.entity;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartyMember extends BaseTime {
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Party party;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyRole partyRole;

    // TODO: 파티 로그

    @Builder
    public PartyMember(Member member, PartyRole partyRole) {
        this.member = member;
        this.partyRole = partyRole;
    }

    public void delete() {
        if (this.party != null) {
            this.party.getPartyMembers().remove(this);
            this.party = null;
        }
    }
}
