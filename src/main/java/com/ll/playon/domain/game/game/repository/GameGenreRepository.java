package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.GameGenre;
import com.ll.playon.domain.game.game.entity.SteamGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameGenreRepository extends JpaRepository<GameGenre, Long> {
    List<GameGenre> findByGame(SteamGame game);
}
