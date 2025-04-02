package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.dto.request.GetGuildListRequest;
import com.ll.playon.domain.guild.guild.entity.Guild;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuildRepositoryCustom {
    Page<Guild> searchGuilds(GetGuildListRequest req, Pageable pageable);
}
