package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.dto.request.GameSearchCondition;
import com.ll.playon.domain.game.game.entity.SteamGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<SteamGame, Long> {
    List<SteamGame> findAllByAppidIn(List<Long> appIds);
    List<SteamGame> findTop5ByAppidIn(List<Long> appIds);
    Optional<SteamGame> findByAppid(Long appid);
    Optional<SteamGame> findSteamGameByAppid(Long appid);
    List<SteamGame> searchByGameName(String keyword);
    Page<SteamGame> searchGames(GameSearchCondition condition, Pageable pageable);

}
