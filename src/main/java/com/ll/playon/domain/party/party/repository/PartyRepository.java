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

    @Query("""
            SELECT p.id
            FROM Party p
            LEFT JOIN PartyTag pt
            ON pt.party = p
            WHERE p.partyStatus = 'PENDING'
            AND p.publicFlag = true
            AND (:partyAt IS NULL OR p.partyAt >= :partyAt)
            AND p.id NOT IN :excludedIds
            AND ((:tagSize = 0) OR (pt.value IN :tagValues))
            GROUP BY p.id
            HAVING (:tagSize = 0 OR COUNT(pt.value) = :tagSize)
            """)
    Page<Long> findPublicPartyIdsExcludingMyParties(
            @Param("excludedIds") List<Long> excludedIds,
            @Param("partyAt") LocalDateTime partyAt,
            @Param("tagValues") List<String> tagValues,
            @Param("tagSize") long tagSize,
            Pageable pageable
    );

    // 성능 비교용 1
//    @Query(value = """
//            SELECT p.id
//            FROM Party p
//            LEFT JOIN PartyTag pt ON pt.party = p
//            WHERE p.partyStatus = 'PENDING'
//            AND p.publicFlag = true
//            AND (:partyAt IS NULL OR p.partyAt >= :partyAt)
//            AND (:tagSize = 0 OR pt.value IN :tagValues)
//            AND p.id NOT IN :excludedIds
//            GROUP BY p.id
//            HAVING COUNT(pt.value) = :tagSize
//            """,
//            countQuery = """
//                    SELECT COUNT(p)
//                    FROM Party p
//                    LEFT JOIN PartyTag pt ON pt.party = p
//                    WHERE p.partyStatus = 'PENDING'
//                    AND p.publicFlag = true
//                    AND (:party IS NULL OR p.partyAt >= :partyAt)
//                    AND (:tagSize = 0 OR pt.value IN :tagValues)
//                    AND p.id NOT IN :excludedIds
//                    GROUP BY p.id
//                    HAVING COUNT(pt.value) = :tagSize
//                    """
//    )
//    Page<Long> findPublicPartyIdsExcludingMyParties(
//            @Param("excludedIds") List<Long> excludedIds,
//            @Param("partyAt") LocalDateTime partyAt,
//            @Param("tagValues") List<String> tagValues,
//            @Param("tagSize") long tagSize,
//            Pageable pageable
//    );

    // 성능 비교용 2
//    @Query(value = """
//            SELECT p.id
//            FROM Party p
//            WHERE p.partyStatus = 'PENDING'
//            AND p.publicFlag = true
//            AND (:partyAt IS NULL OR p.partyAt >= :partyAt)
//            AND (:tagSize = 0 OR (
//                SELECT COUNT(pt.value)
//                FROM PartyTag pt
//                WHERE pt.party = p
//                AND pt.value IN :tagValues
//            ) = :tagSize)
//            AND p.id NOT IN :excludedIds
//            """,
//            countQuery = """
//                    SELECT COUNT(p)
//                    FROM Party p
//                    WHERE p.partyStatus = 'PENDING'
//                    AND p.publicFlag = true
//                    AND (:party IS NULL OR p.partyAt >= :partyAt)
//                    AND (:tagSize = 0 OR (
//                        SELECT COUNT(pt.value)
//                        FROM PartyTag pt
//                        WHERE pt.party = p
//                        AND pt.value IN :tagValues
//                    ) = :tagSize)
//                    AND p.id NOT IN :excludedIds
//                    """
//    )
//    Page<Long> findPublicPartyIdsExcludingMyParties(
//            @Param("excludedIds") List<Long> excludedIds,
//            @Param("partyAt") LocalDateTime partyAt,
//            @Param("tagValues") List<String> tagValues,
//            @Param("tagSize") long tagSize,
//            Pageable pageable
//    );

    @Query("""
            SELECT p.id
            FROM Party p
            WHERE p.partyStatus = 'PENDING'
            AND EXISTS (
                SELECT 1
                FROM PartyMember pm
                WHERE pm.party = p
                AND pm.member.id = :memberId
            )
            AND (:partyAt IS NULL OR p.partyAt >= :partyAt)
            """)
    List<Long> findPartyIdsByMember(
            @Param("memberId") long memberId,
            @Param("partyAt") LocalDateTime partyAt
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

    @Query("SELECT p FROM Party p WHERE p.game = :gameId")
    Page<Party> findByGameId(@Param("gameId") Long gameId, Pageable partyPageable);

    List<Party> findAllByPartyStatusAndPublicFlagTrueOrderByPartyAtAscCreatedAtDesc(PartyStatus partyStatus,
                                                                                    Pageable pageable);
}
