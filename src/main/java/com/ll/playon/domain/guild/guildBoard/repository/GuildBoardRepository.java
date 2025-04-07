package com.ll.playon.domain.guild.guildBoard.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildBoardRepository extends JpaRepository<GuildBoard, Long> {
    Page<GuildBoard> findByGuild(Guild guild, Pageable pageable);
    Page<GuildBoard> findByGuildAndTag(Guild guild, BoardTag tag, Pageable pageable);
    Page<GuildBoard> findByGuildAndTitleContaining(Guild guild, String keyword, Pageable pageable);
    Page<GuildBoard> findByGuildAndTagAndTitleContaining(Guild guild, BoardTag tag, String keyword, Pageable pageable);
}
