package com.ll.playon.domain.game.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameScheduler {

    private final JobLauncher jobLauncher;
    private final Job gameBatchJob;

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00:00
//    @Scheduled(cron = "0 * * * * *")
    public void updateWeeklyGameStats() {
        try {
            jobLauncher.run(gameBatchJob,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters());
        } catch (Exception e) {
            log.error("주간 게임 배치 작업 중 예외 발생", e);
        }
    }
}
