package com.ll.playon.domain.party.partyLog.repository;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartyLogRepository extends JpaRepository<PartyLog, Long> {
    @Query("SELECT pl FROM PartyLog pl WHERE pl.partyMember.party.game = :game")
    Page<PartyLog> findByPartyGame(@Param("game") SteamGame game, Pageable pageable);

}
