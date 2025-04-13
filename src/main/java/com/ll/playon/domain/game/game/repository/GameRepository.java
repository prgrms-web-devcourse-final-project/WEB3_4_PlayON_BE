package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.dto.request.GameSearchCondition;
import com.ll.playon.domain.game.game.entity.SteamGame;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<SteamGame, Long>, GameRepositoryCustom {
    List<SteamGame> findAllByAppidIn(List<Long> appIds);

    Optional<SteamGame> findByAppid(Long appid);

    Optional<SteamGame> findSteamGameByAppid(Long appid);

    List<SteamGame> searchByGameName(String keyword);

    Page<SteamGame> searchGames(GameSearchCondition condition, Pageable pageable);

    List<SteamGame> findAllByIdIn(List<Long> gameIds);

    @Query("""
            SELECT s.appid
            FROM SteamGame s
            """)
    Page<Long> findAllAppIds(Pageable pageable);
}
