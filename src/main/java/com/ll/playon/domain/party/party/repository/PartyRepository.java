package com.ll.playon.domain.party.party.repository;

import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.type.PartyStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartyRepository extends JpaRepository<Party, Long> {

    // 1개 이상 태그만 충족하면 검색 가능, 우선 보류
//    @Query("""
//            SELECT p.id
//            FROM Party p
//            WHERE p.partyStatus = 'PENDING'
//            AND (:partyAt IS NULL OR (p.partyAt >= :partyAt))
//            AND EXISTS (
//                SELECT 1
//                FROM PartyTag pt
//                WHERE pt.party = p
//                AND pt.value IN :tagValues
//            )
//            """)
//    Page<Long> findPartyIdsWithFilter(
//            @Param("partyAt") LocalDateTime partyAt,
//            @Param("tagValues") List<String> tagValues,
//            Pageable pageable
//    );

    // 필터 조건 사용 쿼리
    @Query("""
            SELECT p.id
            FROM Party p
            JOIN PartyTag pt ON pt.party = p
            WHERE p.partyStatus = 'PENDING'
            AND (:partyAt IS NULL OR (p.partyAt >= :partyAt))
            AND pt.value IN :tagValues
            GROUP BY p.id
            HAVING COUNT(pt.value) = :tagSize
            """)
    Page<Long> findPartyIdsWithFilter(
            @Param("partyAt") LocalDateTime partyAt,
            @Param("tagValues") List<String> tagValues,
            @Param("tagSize") long tagSize,
            Pageable pageable
    );

    // 추후 성능 비교
//    @Query("""
//            SELECT p.id
//            FROM Party p
//            WHERE p.partyStatus = 'PENDING'
//            AND (:partyAt IS NULL OR (p.partyAt >= :partyAt))
//            AND (
//                SELECT COUNT(pt)
//                FROM PartyTag pt
//                WHERE pt.party = p
//                AND pt.value IN :tagValues
//            ) = :tagSize
//            """)
//    Page<Long> findPartyIdsWithFilter(
//            @Param("partyAt") LocalDateTime partyAt,
//            @Param("tagValues") List<String> tagValues,
//            @Param("tagSize") long tagSize,
//            Pageable pageable
//    );

    @Query("""
            SELECT p.id
            FROM Party p
            WHERE p.partyStatus = 'PENDING'
            AND (:partyAt IS NULL OR (p.partyAt >= :partyAt))
            """)
    Page<Long> findPartyIdsWithoutFilter(
            @Param("partyAt") LocalDateTime partyAt,
            Pageable pageable
    );

    @Query("""
            SELECT p
            FROM Party p
            WHERE p.id IN :partyIds
            """)
    List<Party> findPartiesByIds(@Param("partyIds") List<Long> partyIds);

    @Query("""
            SELECT pt
            FROM PartyTag pt
            JOIN FETCH pt.party p
            WHERE p.id IN :partyIds
            """)
    List<PartyTag> findPartyTagsByPartyIds(@Param("partyIds") List<Long> partyIds);

    @Query("""
            SELECT pm
            FROM PartyMember pm
            JOIN FETCH pm.party p
            WHERE p.id IN :partyIds
            AND pm.partyRole <> 'PENDING'
            """)
    List<PartyMember> findPartyMembersByPartyIds(@Param("partyIds") List<Long> partyIds);

    List<Party> findAllByPartyStatusOrderByPartyAtDescCreatedAtDesc(PartyStatus partyStatus, Pageable pageable);
}
