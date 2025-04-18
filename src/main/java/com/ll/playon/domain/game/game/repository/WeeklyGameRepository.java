package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.WeeklyPopularGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WeeklyGameRepository extends JpaRepository<WeeklyPopularGame, Long> {
    @Query("SELECT appid FROM WeeklyPopularGame WHERE weekStartDate = :weekStartDate ORDER BY playCount DESC")
    List<Long> findTopGameIdsByWeek(@Param("weekStartDate") LocalDate weekStartDate);
}
