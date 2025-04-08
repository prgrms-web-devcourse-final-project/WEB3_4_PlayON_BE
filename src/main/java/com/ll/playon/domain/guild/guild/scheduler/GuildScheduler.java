package com.ll.playon.domain.guild.guild.scheduler;

import com.ll.playon.domain.guild.guild.entity.WeeklyPopularGuild;
import com.ll.playon.domain.guild.guild.repository.WeeklyPopularGuildRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardRepository;
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
public class GuildScheduler {
    final int limit = 3;
    private final WeeklyPopularGuildRepository weeklyPopularGuildRepository;
    private final GuildBoardRepository guildBoardRepository;

    @Scheduled(cron = "0 0 3 * * MON") // 매주 월요일 03:00
//    @Scheduled(cron = "0 * * * * *")
    public void updateWeeklyGuildStats() {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDateTime fromDate = weekStart.minusWeeks(1).atStartOfDay(); // 지난주
        LocalDateTime toDate = weekStart.atStartOfDay(); // 이번주

        updatePopularGuild(fromDate, toDate, weekStart);
    }

    // 일주일간 커뮤니티 글 많은 길드
    private void updatePopularGuild(LocalDateTime fromDate, LocalDateTime toDate, LocalDate weekStart) {
        List<WeeklyPopularGuild> list = guildBoardRepository.findTopGuildsByPartyLastWeek(fromDate, toDate, PageRequest.of(0, limit))
                .stream()
                .map(row -> WeeklyPopularGuild.builder()
                        .guildId(row.getGuildId())
                        .postCount(row.getPostCount())
                        .weekStartDate(weekStart)
                        .build())
                .toList();
        weeklyPopularGuildRepository.saveAll(list);
    }
}
