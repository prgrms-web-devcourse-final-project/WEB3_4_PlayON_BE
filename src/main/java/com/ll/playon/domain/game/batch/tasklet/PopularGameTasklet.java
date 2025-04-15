package com.ll.playon.domain.game.batch.tasklet;

import com.ll.playon.domain.game.game.entity.WeeklyPopularGame;
import com.ll.playon.domain.game.game.repository.WeeklyGameRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularGameTasklet implements Tasklet {

    private final int limit = 3;

    private final PartyRepository partyRepository;
    private final WeeklyGameRepository weeklyGameRepository;

    /**
     * 매주 월요일 00:00에 실행되는 배치 작업
     * 지난주 유저가 파티에서 많이 선택한 게임을 조회하여 저장
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDateTime fromDate = weekStart.minusWeeks(1).atStartOfDay(); // 지난주
        LocalDateTime toDate = weekStart.atStartOfDay(); // 이번주

        log.info("[PopularGameBatch] 인기 게임 통계 배치 시작 => 기간: {} ~ {}", fromDate, toDate);

        List<WeeklyPopularGame> list = partyRepository.findTopGamesByPartyLastWeek(fromDate, toDate, PageRequest.of(0, limit))
                .stream()
                .map(row -> WeeklyPopularGame.builder()
                        .appid(row.getAppid())
                        .playCount(row.getPlayCount())
                        .weekStartDate(weekStart)
                        .build())
                .toList();
        weeklyGameRepository.saveAll(list);

        log.info("[PopularGameBatch] 인기 게임 통계 저장 완료");

        return RepeatStatus.FINISHED;
    }
}
