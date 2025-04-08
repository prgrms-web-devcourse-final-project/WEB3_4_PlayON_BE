package com.ll.playon.domain.guild.guildBoard.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.projection.TopGuildPostProjection;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GuildBoardRepository extends JpaRepository<GuildBoard, Long> {
    @EntityGraph(attributePaths = {"author.member"})
    Page<GuildBoard> findByGuild(Guild guild, Pageable pageable);

    @EntityGraph(attributePaths = {"author.member"})
    Page<GuildBoard> findByGuildAndTag(Guild guild, BoardTag tag, Pageable pageable);

    Page<GuildBoard> findByGuildAndTitleContaining(Guild guild, String keyword, Pageable pageable);
    Page<GuildBoard> findByGuildAndTagAndTitleContaining(Guild guild, BoardTag tag, String keyword, Pageable pageable);

    int countByAuthor(GuildMember author);

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
}
