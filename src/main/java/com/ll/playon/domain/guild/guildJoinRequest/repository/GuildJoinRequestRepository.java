package com.ll.playon.domain.guild.guildJoinRequest.repository;

import com.ll.playon.domain.guild.guildJoinRequest.entity.GuildJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildJoinRequestRepository extends JpaRepository<GuildJoinRequest, Long> {
}
