package com.ll.playon.domain.chat.entity;

import com.ll.playon.domain.party.party.entity.PartyMember;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "chat_member",
        indexes = {
                @Index(name = "idx_chat_member_party_room_id_party_member_id", columnList = "party_room_id, party_member_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ChatMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PartyRoom partyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private PartyMember partyMember;

    // 반드시 파티룸과 파티멤버가 존재해야 생성 가능
    @Builder
    public ChatMember(PartyRoom partyRoom, PartyMember partyMember) {
        this.partyRoom = partyRoom;
        this.partyMember = partyMember;
    }
}
