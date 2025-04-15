package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.dto.request.GameSearchCondition;
import com.ll.playon.domain.game.game.entity.SteamGame;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
            SELECT g.id
            FROM SteamGame g
            WHERE (:keyword IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:isMacSupported IS NULL OR g.isMacSupported = :isMacSupported)
            AND (:releasedAfter IS NULL OR g.releaseDate >= :releasedAfter)
            AND (:releaseStatus IS NULL OR
                ((:releaseStatus = 'RELEASED' AND g.releaseDate <= CURRENT_DATE) OR
                (:releaseStatus = 'UNRELEASED' AND g.releaseDate > CURRENT_DATE)))
            AND (:playerType IS NULL OR
                ((:playerType = 'SINGLE' AND g.isSinglePlayer = TRUE) OR
                (:playerType = 'MULTI' AND g.isMultiPlayer = TRUE)))
            AND (:genres IS NULL OR EXISTS (
                SELECT 1
                FROM SteamGenre sg
                JOIN sg.games game
                WHERE game.id = g.id
                AND sg.name IN :genres
                GROUP BY sg.id
                HAVING COUNT(sg.name) = :genreSize
            ))
            """)
    Page<Long> findGameIdsWithAllFilter(
            @Param("keyword") String keyword,
            @Param("isMacSupported") Boolean isMacSupported,
            @Param("releasedAfter") LocalDate releasedAfter,
            @Param("releaseStatus") String releaseStatus,
            @Param("playerType") String playerType,
            @Param("genres") List<String> genres,
            @Param("genreSize") int genreSize,
            Pageable pageable
    );

    @Query("""
            SELECT g
            FROM SteamGame g
            LEFT JOIN FETCH g.genres
            WHERE g.id IN :ids
            """)
    List<SteamGame> findSteamGameByIds(@Param("ids") List<Long> ids);
}
