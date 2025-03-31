package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuildRepository extends JpaRepository<Guild, Long> {

    boolean existsByName(String name);

    Optional<Guild> findByIdAndIsDeletedFalse(Long guildId);
}
