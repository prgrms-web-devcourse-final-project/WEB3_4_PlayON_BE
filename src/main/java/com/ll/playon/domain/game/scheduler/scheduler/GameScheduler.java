package com.ll.playon.domain.game.scheduler.scheduler;

import com.ll.playon.domain.game.game.entity.WeeklyPopularGame;
import com.ll.playon.domain.game.scheduler.repository.WeeklyGameRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class GameScheduler {
    private final PartyRepository partyRepository;
    private final WeeklyGameRepository weeklyGameRepository;

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00:00
//    @Scheduled(cron = "0 * * * * *")
    public void updateWeeklyGameStats() {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDateTime fromDate = weekStart.minusWeeks(1).atStartOfDay(); // 지난주
        LocalDateTime toDate = weekStart.atStartOfDay(); // 이번주

        updatePopularGames(fromDate, toDate, weekStart);
    }

    public void updatePopularGames(LocalDateTime fromDate, LocalDateTime toDate, LocalDate weekStart) {
        final int limit = 3;

        List<WeeklyPopularGame> list = partyRepository.findTopGamesByPartyLastWeek(fromDate, toDate, PageRequest.of(0, limit))
                .stream()
                .map(row -> WeeklyPopularGame.builder()
                        .appid((Long) row.get("appid"))
                        .playCount(((Number) row.get("playCount")).longValue())
                        .weekStartDate(weekStart)
                        .build())
                .toList();

        weeklyGameRepository.saveAll(list);
    }
}
