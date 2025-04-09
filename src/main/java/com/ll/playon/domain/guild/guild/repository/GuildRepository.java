package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long>, GuildRepositoryCustom {

    boolean existsByName(String name);

    Optional<Guild> findByIdAndIsDeletedFalse(Long guildId);

    @EntityGraph(attributePaths = {"guildTags"})
    @Query("SELECT g FROM Guild g WHERE g.id = :guildId AND g.isDeleted = false")
    Optional<Guild> findWithTagsById(@Param("guildId") Long guildId);

    List<Guild> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT g FROM Guild g WHERE g.game.appid = :appid AND g.isDeleted = false AND g.isPublic = true ORDER BY g.id DESC")
    List<Guild> findTopNByGameAppid(@Param("appid") Long appid, Pageable pageable);
}
