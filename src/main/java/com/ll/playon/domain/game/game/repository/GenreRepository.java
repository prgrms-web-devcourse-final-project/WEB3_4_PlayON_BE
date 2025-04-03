package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.SteamGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<SteamGenre, Long> {
    Optional<SteamGenre> findByName(String mostFrequentGenre);
}
