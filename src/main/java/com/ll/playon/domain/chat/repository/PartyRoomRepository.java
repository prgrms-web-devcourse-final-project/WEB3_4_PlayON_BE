package com.ll.playon.domain.chat.repository;

import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.party.party.entity.Party;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartyRoomRepository extends JpaRepository<PartyRoom, Long> {
    Optional<PartyRoom> findByParty(Party party);

    @Query("""
            SELECT pr.id
            from PartyRoom pr
            JOIN pr.party p
            WHERE p.partyAt <= :deadlineTime
            """)
    List<Long> findDeletablePartyRooms(@Param("deadlineTime") LocalDateTime deadlineTime);
}
