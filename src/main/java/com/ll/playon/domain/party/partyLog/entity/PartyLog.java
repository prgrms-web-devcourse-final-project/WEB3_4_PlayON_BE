package com.ll.playon.domain.party.partyLog.entity;

import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.partyLog.dto.request.PutPartyLogRequest;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_party_log_party_member_id", columnList = "party_member_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartyLog extends BaseTime {
    @OneToOne(fetch = FetchType.LAZY)
    private PartyMember partyMember;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder
    public PartyLog(PartyMember partyMember, String comment, String content) {
        this.partyMember = partyMember;
        this.comment = comment;
        this.content = content;
    }

    public void delete() {
        this.partyMember.setPartyLog(null);
        this.partyMember = null;
    }

    public void update(PutPartyLogRequest request) {
        this.comment = request.comment();
        this.content = request.content();
    }
}
