package com.ll.playon.domain.party.party.repository;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.projection.TopPartyGameProjection;
import com.ll.playon.domain.game.game.projection.TopPlaytimeGameProjection;
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
            WHERE p.partyStatus = 'PENDING'
            AND p.publicFlag = true
            AND (:partyAt IS NULL OR p.partyAt >= :partyAt)
            AND p.id NOT IN :excludedIds
            AND (1=:noTagCondition OR p.id IN (
                SELECT pt.party.id
                FROM PartyTag pt
                WHERE pt.value IN :tagValues
                GROUP BY pt.party.id
                HAVING COUNT(pt.value) = :tagSize
            ))
            AND (:gameId IS NULL OR p.game.id = :gameId)
            AND (1=:noGenreCondition OR p.game.id IN (
                SELECT sg.id
                FROM SteamGame sg
                JOIN sg.genres g
                WHERE g.name IN :genres
                GROUP BY sg.id
                HAVING COUNT(g.name) = :genreSize
            ))
            """)
    Page<Long> findPartyIdsByAllConditions(
            @Param("excludedIds") List<Long> excludedIds,
            @Param("partyAt") LocalDateTime partyAt,
            @Param("tagValues") List<String> tagValues,
            @Param("tagSize") long tagSize,
            @Param("noTagCondition") int noTagCondition,
            @Param("gameId") Long gameId,
            @Param("genres") List<String> genres,
            @Param("genreSize") int genreSize,
            @Param("noGenreCondition") int noGenreCondition,
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
            AND pm.partyRole = 'MEMBER'
            OR pm.partyRole = 'OWNER'
            """)
    List<PartyMember> findPartyMembersByPartyIds(@Param("partyIds") List<Long> partyIds);

    Page<Party> findByGame(SteamGame game, Pageable pageable);

    List<Party> findAllByPartyStatusAndPublicFlagTrueOrderByPartyAtAscCreatedAtDesc(PartyStatus partyStatus,
                                                                                    Pageable pageable);

    List<Party> findAllByPartyStatusAndPublicFlagTrueOrderByPartyAtDescCreatedAtDesc(PartyStatus partyStatus,
                                                                                     Pageable pageable);

    @Query("""
                SELECT p
                FROM Party p
                WHERE p.id IN :partyIds
                  AND p.publicFlag = TRUE
                  AND p.partyStatus = 'COMPLETED'
                ORDER BY p.createdAt DESC
            """)
    List<Party> findPublicCompletedPartiesIn(@Param("partyIds") List<Long> partyIds, Pageable pageable);

    @Query("""
                SELECT p.game.appid AS appid, COUNT(p) AS playCount
                FROM Party p
                WHERE p.createdAt >= :fromDate
                  AND p.createdAt < :toDate
                GROUP BY p.game.appid
                ORDER BY playCount DESC
            """)
    List<TopPartyGameProjection> findTopGamesByPartyLastWeek(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query(value = """
                SELECT p.game_id AS appid,
                       SUM(TIMESTAMPDIFF(SECOND, p.party_at, p.ended_at)) / 3600 AS playtime
                FROM party p
                WHERE p.party_at >= :fromDate AND p.party_at < :toDate
                  AND p.is_public = true
                  AND p.party_status = 'COMPLETED'
                  AND p.ended_at IS NOT NULL
                GROUP BY p.game_id
                ORDER BY playtime DESC
            """,
            nativeQuery = true)
    List<TopPlaytimeGameProjection> findTopGamesByPlaytimeLastWeek(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}
