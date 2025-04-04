package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.SteamGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;

public interface GenreRepository extends JpaRepository<SteamGenre, Long> {
    Optional<SteamGenre> findByName(String mostFrequentGenre);
    List<SteamGenre> findByIdIn(List<Long> genreIds);
    
    Optional<SteamGenre> findByGenre(String genre);

    default SteamGenre findOrCreate(String genre) {
        return findByGenre(genre)
                .orElseGet(() -> save(new SteamGenre(genre)));
    }
}
