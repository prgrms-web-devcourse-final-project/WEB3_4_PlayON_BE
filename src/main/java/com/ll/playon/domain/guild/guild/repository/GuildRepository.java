package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long>, GuildRepositoryCustom {

    boolean existsByName(String name);

    Optional<Guild> findByIdAndIsDeletedFalse(Long guildId);
}
