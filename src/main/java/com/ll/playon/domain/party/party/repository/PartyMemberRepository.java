package com.ll.playon.domain.party.party.repository;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    Optional<PartyMember> findByMemberAndParty(Member actor, Party party);
}
