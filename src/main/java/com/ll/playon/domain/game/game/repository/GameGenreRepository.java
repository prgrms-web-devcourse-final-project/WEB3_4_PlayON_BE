package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.GameGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameGenreRepository extends JpaRepository<GameGenre, Long> {
}
