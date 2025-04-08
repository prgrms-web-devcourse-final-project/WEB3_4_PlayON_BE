package com.ll.playon.domain.guild.guild.repository;

import com.ll.playon.domain.guild.guild.entity.WeeklyPopularGuild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WeeklyPopularGuildRepository extends JpaRepository<WeeklyPopularGuild, Long> {
    @Query("SELECT guildId FROM WeeklyPopularGuild WHERE weekStartDate = :week ORDER BY postCount DESC")
    List<Long> findGuildIdsByWeek(@Param("week") LocalDate weekStartDate);
}
