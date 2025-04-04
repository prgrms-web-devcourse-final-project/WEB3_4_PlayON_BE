package com.ll.playon.domain.party.partyLog;

import com.ll.playon.domain.party.partyLog.entity.PartyLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyLogRepository extends JpaRepository<PartyLog, Long> {
}
