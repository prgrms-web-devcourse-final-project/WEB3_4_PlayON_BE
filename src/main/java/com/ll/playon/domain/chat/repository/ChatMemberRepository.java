package com.ll.playon.domain.chat.repository;

import com.ll.playon.domain.chat.dto.ChatMemberCountDto;
import com.ll.playon.domain.chat.entity.ChatMember;
import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.party.party.entity.PartyMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    Boolean existsByPartyRoomAndPartyMember(PartyRoom partyRoom, PartyMember partyMember);

    List<ChatMember> findAllByPartyRoom(PartyRoom partyRoom);

    Optional<ChatMember> findByPartyRoomAndPartyMember(PartyRoom partyRoom, PartyMember partyMember);

    Long countByPartyRoom(PartyRoom partyRoom);

    @Query("""
            SELECT new com.ll.playon.domain.chat.dto.ChatMemberCountDto(cm.partyRoom.id, COUNT(cm))
            FROM ChatMember cm
            WHERE cm.partyRoom.id IN :candidateIds
            GROUP BY cm.partyRoom.id
            """)
    List<ChatMemberCountDto> countByPartyRoomIds(@Param("candidateIds") List<Long> candidateIds);
}
