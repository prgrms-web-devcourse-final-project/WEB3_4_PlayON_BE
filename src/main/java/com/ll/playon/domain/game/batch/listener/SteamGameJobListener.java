package com.ll.playon.domain.game.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class SteamGameJobListener implements JobExecutionListener {
    private static final Logger memoryLog = LoggerFactory.getLogger("memoryLogger");

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("✅ Batch 시작 steamGameJob 시작: {}", jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long totalWriteCount = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum();

        if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
            long duration = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();

            log.info("✅ Batch 종료 steamGameJob 종료: {}", jobExecution.getEndTime());
            log.info("✅ 총 처리 건수: {}건", totalWriteCount);
            log.info("✅ 총 소요 시간: {}ms", duration);
            logMemoryUsage("clear() ⭕️");
//            logMemoryUsage("원래 버전️");

        } else {
            log.info("✅ Batch 종료 steamGameJob 종료");
            log.info("✅ 총 처리건수: {}건", totalWriteCount);
            logMemoryUsage("clear() ⭕️");
//            logMemoryUsage("원래 버전️");
        }
    }

    private void logMemoryUsage(String phase) {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory() / (1024 * 1024);
        long free = runtime.freeMemory() / (1024 * 1024);
        long used = total - free;

        memoryLog.info("[{}] 메모리 사용량 - Used: {}MB, Free: {}MB, Total: {}MB", phase, used, free, total);
    }
}
