package com.ll.playon.domain.party.party.repository;

import com.ll.playon.domain.party.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
}
