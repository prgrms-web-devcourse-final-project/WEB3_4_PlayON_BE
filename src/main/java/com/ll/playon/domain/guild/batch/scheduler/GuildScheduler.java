package com.ll.playon.domain.guild.batch.scheduler;

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
public class GuildScheduler {

    private final JobLauncher jobLauncher;
    private final Job guildBatchJob;

    @Scheduled(cron = "0 0 3 * * MON") // 매주 월요일 03:00
//    @Scheduled(cron = "0 * * * * *")
    public void updatePopularGuildBatch() {
        try {
            jobLauncher.run(guildBatchJob,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters());
        } catch (Exception e) {
            log.error("길드 배치 작업 중 예외 발생", e);
        }
    }
}
