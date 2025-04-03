package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.SteamGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<SteamGame, Long> {
    Optional<Object> findByAppid(Long appid);
}
