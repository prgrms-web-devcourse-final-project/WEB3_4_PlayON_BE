package com.ll.playon.domain.party.party.repository;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.type.PartyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
//    @Query("""
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
//            """)
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

    // 성능 비교용 3
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
            LEFT JOIN SteamGame sg
            ON sg.id = p.game.id
            LEFT JOIN sg.genres g
            ON g.name IN :genres
            WHERE p.id IN :partyIds
            AND (:gameId IS NULL OR p.game.id = :gameId)
            GROUP BY p.id
            HAVING (:genreSize = 0 OR COUNT(g.name) = :genreSize)
            """)
    Page<Long> findPublicPartiesFilteredByGame(
            @Param("partyIds") List<Long> partyIds,
            @Param("gameId") Long gameId,
            @Param("genres") List<String> genres,
            @Param("genreSize") int genreSize,
            Pageable pageable
    );

    @Query("""
            SELECT p.id
            FROM Party p
            LEFT JOIN PartyMember pm
            ON pm.party = p
            WHERE p.partyStatus = 'PENDING'
            AND pm.member.id = :memberId
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
            AND pm.partyRole != 'PENDING'
            """)
    List<PartyMember> findPartyMembersByPartyIds(@Param("partyIds") List<Long> partyIds);

    Page<Party> findByGame(SteamGame game, Pageable pageable);


    @Query("""
        SELECT p.game.appid AS appid, COUNT(p) AS playCount
        FROM Party p
        WHERE p.createdAt >= :fromDate
          AND p.createdAt < :toDate
        GROUP BY p.game.appid
        ORDER BY playCount DESC
    """)
    List<Map<String, Object>> findTopGamesByPartyLastWeek(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    List<Party> findAllByPartyStatusAndPublicFlagTrueOrderByPartyAtAscCreatedAtDesc(PartyStatus partyStatus,
                                                                                    Pageable pageable);

    List<Party> findAllByPartyStatusAndPublicFlagTrueOrderByPartyAtDescCreatedAtDesc(PartyStatus partyStatus,
                                                                                     Pageable pageable);

}
