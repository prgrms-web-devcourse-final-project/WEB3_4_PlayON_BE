package com.ll.playon.domain.party.party.entity;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "party_member",
        indexes = {
                @Index(name = "idx_party_member_party_id_role", columnList = "party_id, party_role"),
                @Index(name = "idx_party_member_party_id_member_id", columnList = "party_id, member_id")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartyMember extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Party party;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyRole partyRole;

    @Column(nullable = false)
    private Integer mvpPoint;

    @OneToOne(mappedBy = "partyMember", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private PartyLog partyLog;

    @Builder
    public PartyMember(Member member, PartyRole partyRole, Integer mvpPoint) {
        this.member = member;
        this.partyRole = partyRole;
        this.mvpPoint = mvpPoint;
    }

    public void voteMvp() {
        ++this.mvpPoint;
    }

    public boolean isOwn(Member member) {
        return this.party != null && this.member.equals(member);
    }

    public void delete() {
        if (this.party != null) {
            this.party.deletePartyMember(this);
        }
    }

    public void promoteRole(PartyRole partyRole) {
        if ((this.partyRole.equals(PartyRole.PENDING) || this.partyRole.equals(PartyRole.INVITER))
            && (partyRole.equals(PartyRole.OWNER) || partyRole.equals(PartyRole.MEMBER))) {
            this.party.updateTotal(true);
        }

        this.partyRole = partyRole;
    }
}
