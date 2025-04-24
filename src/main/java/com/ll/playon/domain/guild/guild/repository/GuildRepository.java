package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.Guild;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
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

    @Query("SELECT g FROM Guild g WHERE g.game.appid = :appid AND g.isDeleted = false AND g.isPublic = true ORDER BY g.id DESC")
    List<Guild> findTopNByGameAppid(@Param("appid") Long appid, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Guild g WHERE g.id=:id")
    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout",value="3000"))
    Optional<Guild> findByIdForUpdate(@Param("id") Long id);
}
