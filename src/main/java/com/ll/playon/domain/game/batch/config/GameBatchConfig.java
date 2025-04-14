package com.ll.playon.domain.game.batch.config;

import com.ll.playon.domain.game.batch.tasklet.LongPlaytimeGameTasklet;
import com.ll.playon.domain.game.batch.tasklet.PopularGameTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class GameBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PopularGameTasklet popularGameTasklet;
    private final LongPlaytimeGameTasklet longPlaytimeGameTasklet;

    @Bean
    public Job popularGameBatchJob() {
        return new JobBuilder("gameBatchJob", jobRepository)
                .start(popularGameStep())
                .next(longPlaytimeGameStep())
                .build();
    }

    @Bean
    public Step popularGameStep() {
        return new StepBuilder("popularGameStep", jobRepository)
                .tasklet(popularGameTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job longPlaytimeGameJob() {
        return new JobBuilder("longPlaytimeGameJob", jobRepository)
                .start(longPlaytimeGameStep())
                .build();
    }

    @Bean
    public Step longPlaytimeGameStep() {
        return new StepBuilder("longPlaytimeGameStep", jobRepository)
                .tasklet(longPlaytimeGameTasklet, transactionManager)
                .build();
    }
}
