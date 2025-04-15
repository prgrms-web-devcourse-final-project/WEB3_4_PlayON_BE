package com.ll.playon.domain.guild.batch.tesklet;

import com.ll.playon.domain.guild.guild.entity.WeeklyPopularGuild;
import com.ll.playon.domain.guild.guild.repository.WeeklyPopularGuildRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GuildTasklet implements Tasklet {

    private final int limit = 3;

    private final GuildBoardRepository guildBoardRepository;
    private final WeeklyPopularGuildRepository weeklyPopularGuildRepository;

    /**
     * 매주 월요일 00:00에 실행되는 배치 작업
     * 지난주 유저가 많이 활동한 길드를 조회하여 저장
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDateTime fromDate = weekStart.minusWeeks(1).atStartOfDay(); // 지난주
        LocalDateTime toDate = weekStart.atStartOfDay(); // 이번주

        List<WeeklyPopularGuild> list = guildBoardRepository.findTopGuildsByPartyLastWeek(fromDate, toDate, PageRequest.of(0, limit))
                .stream()
                .map(row -> WeeklyPopularGuild.builder()
                        .guildId(row.getGuildId())
                        .postCount(row.getPostCount())
                        .weekStartDate(weekStart)
                        .build())
                .toList();
        weeklyPopularGuildRepository.saveAll(list);

        return RepeatStatus.FINISHED;
    }
}
