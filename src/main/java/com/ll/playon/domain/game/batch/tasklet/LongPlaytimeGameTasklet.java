package com.ll.playon.domain.game.batch.tasklet;

import com.ll.playon.domain.game.game.entity.WeeklyLongPlaytimeGame;
import com.ll.playon.domain.game.game.repository.LongPlaytimeGameRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
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
public class LongPlaytimeGameTasklet implements Tasklet {

    private final int limit = 3;

    private final PartyRepository partyRepository;
    private final LongPlaytimeGameRepository longPlaytimeGameRepository;

    /**
     * 매주 월요일 00:00에 실행되는 배치 작업
     * 지난주 플레이 타임이 긴 게임을 조회하여 저장
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDateTime fromDate = weekStart.minusWeeks(1).atStartOfDay(); // 지난주
        LocalDateTime toDate = weekStart.atStartOfDay(); // 이번주

        List<WeeklyLongPlaytimeGame> list = partyRepository.findTopGamesByPlaytimeLastWeek(fromDate, toDate, PageRequest.of(0, limit))
                .stream()
                .map(row -> WeeklyLongPlaytimeGame.builder()
                        .appid(row.getAppid())
                        .totalPlaytime(row.getPlaytime())
                        .weekStartDate(weekStart)
                        .build())
                .toList();
        longPlaytimeGameRepository.saveAll(list);
        return RepeatStatus.FINISHED;
    }
}
