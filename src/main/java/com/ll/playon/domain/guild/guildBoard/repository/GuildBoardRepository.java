package com.ll.playon.domain.guild.guildBoard.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.projection.TopGuildPostProjection;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GuildBoardRepository extends JpaRepository<GuildBoard, Long> {

    @Query("SELECT gb FROM GuildBoard gb " +
            "LEFT JOIN FETCH gb.author a " +
            "LEFT JOIN FETCH a.member " +
            "WHERE gb.id = :boardId")
    Optional<GuildBoard> findWithAuthorAndMemberById(Long boardId);

    int countByAuthor(GuildMember author);

    @Query("""
    SELECT gb FROM GuildBoard gb
    WHERE gb.guild = :guild
    AND (:tag IS NULL OR gb.tag = :tag)
    AND (:keyword IS NULL OR gb.title LIKE %:keyword%)
""")
    Page<GuildBoard> searchWithFilter(
            @Param("guild") Guild guild,
            @Param("tag") BoardTag tag,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        SELECT gb.guild.id AS guildId, COUNT(gb) AS postCount
        FROM GuildBoard gb
        WHERE gb.createdAt >= :fromDate AND gb.createdAt < :toDate
          AND gb.guild.isDeleted = false
          AND gb.guild.isPublic = true
        GROUP BY gb.guild.id
        ORDER BY COUNT(gb) DESC
    """)
    List<TopGuildPostProjection> findTopGuildsByPartyLastWeek(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    List<GuildBoard> findTop2ByGuildIdAndTagOrderByCreatedAtDesc(Long guildId, BoardTag boardTag);
    List<GuildBoard> findTop4ByGuildIdOrderByCreatedAtDesc(Long guildId);
    void deleteByAuthor(GuildMember author);
}
