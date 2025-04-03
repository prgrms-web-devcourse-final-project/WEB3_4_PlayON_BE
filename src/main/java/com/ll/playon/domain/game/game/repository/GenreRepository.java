package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.SteamGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<SteamGenre, Long> {
}
