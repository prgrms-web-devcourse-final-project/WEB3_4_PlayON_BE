package com.ll.playon.domain.guild.guildBoard.repository;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardLike;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuildBoardLikeRepository extends JpaRepository<GuildBoardLike, Long> {
    Optional<GuildBoardLike> findByGuildMemberAndBoard(GuildMember guildMember, GuildBoard board);
}
