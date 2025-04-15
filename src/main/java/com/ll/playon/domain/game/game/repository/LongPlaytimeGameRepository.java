package com.ll.playon.domain.game.game.repository;

import com.ll.playon.domain.game.game.entity.WeeklyLongPlaytimeGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LongPlaytimeGameRepository extends JpaRepository<WeeklyLongPlaytimeGame, Long> {
    @Query("SELECT appid FROM WeeklyLongPlaytimeGame WHERE weekStartDate = :week ORDER BY totalPlaytime DESC")
    List<Long> findAppIdsByWeek(@Param("week") LocalDate weekStartDate);
}
