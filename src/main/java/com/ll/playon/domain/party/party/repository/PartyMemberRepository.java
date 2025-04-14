package com.ll.playon.domain.party.party.repository;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    Optional<PartyMember> findByMemberAndParty(Member actor, Party party);

    // 내가 참여한 파티 ID
    @Query("""
                SELECT DISTINCT pm.party.id
                FROM PartyMember pm
                WHERE pm.member.id = :memberId
                  AND pm.partyRole IN ('OWNER','MEMBER')
            """)
    List<Long> findPartyIdsByMemberId(@Param("memberId") Long memberId);

    // 특정 파티 ID 목록에 참여했던 멤버
    @Query("""
                SELECT DISTINCT pm.member.id
                FROM PartyMember pm
                WHERE pm.party.id IN :partyIds
                  AND pm.member.id <> :myId
                  AND pm.partyRole IN ('OWNER','MEMBER')
            """)
    List<Long> findMemberIdsInPartiesExceptMe(@Param("candidateIds") List<Long> partyIds, @Param("myId") Long myId);

    // 그 멤버들이 참여한 파티 중에서 내가 참여한 파티 제외
    @Query("""
                SELECT DISTINCT pm.party.id
                FROM PartyMember pm
                WHERE pm.member.id IN :memberIds
                  AND pm.party.id NOT IN :excludePartyIds
                  AND pm.partyRole IN ('OWNER','MEMBER')
            """)
    List<Long> findPartyIdsByMembersExceptPartyIds(
            @Param("memberIds") List<Long> memberIds,
            @Param("excludePartyIds") List<Long> excludePartyIds
    );
}
