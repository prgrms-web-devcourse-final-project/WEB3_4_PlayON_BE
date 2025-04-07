package com.ll.playon.domain.game.scheduler.scheduler;

import com.ll.playon.domain.game.scheduler.WeeklyGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class GameScheduler {
    private final WeeklyGameService weeklyGameService;

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00:00
//    @Scheduled(cron = "0 * * * * *")
    public void updateWeeklyGameStats() {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDateTime fromDate = weekStart.minusWeeks(1).atStartOfDay(); // 지난주
        LocalDateTime toDate = weekStart.atStartOfDay(); // 이번주

        weeklyGameService.updatePopularGames(fromDate, toDate, weekStart);
    }
}
