package com.ll.playon.domain.guild.guildBoard.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildBoardRepository extends JpaRepository<GuildBoard, Long> {
    @EntityGraph(attributePaths = {"author.member"})
    Page<GuildBoard> findByGuild(Guild guild, Pageable pageable);

    @EntityGraph(attributePaths = {"author.member"})
    Page<GuildBoard> findByGuildAndTag(Guild guild, BoardTag tag, Pageable pageable);

}