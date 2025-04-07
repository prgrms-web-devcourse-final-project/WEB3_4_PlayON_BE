package com.ll.playon.domain.guild.guildBoard.repository;

import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuildBoardCommentRepository extends JpaRepository<GuildBoardComment, Long> {
//    List<GuildBoardComment> findByBoardOrderByCreatedAtAsc(GuildBoard board);
    int countByBoard(GuildBoard board);
}
