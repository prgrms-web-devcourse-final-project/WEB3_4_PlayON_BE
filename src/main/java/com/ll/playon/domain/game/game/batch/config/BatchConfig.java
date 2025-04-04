package com.ll.playon.domain.game.game.batch.config;

import com.ll.playon.domain.game.game.batch.processor.SteamGameProcessor;
import com.ll.playon.domain.game.game.dto.SteamCsvDto;
import com.ll.playon.domain.game.game.entity.SteamGame;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FlatFileItemReader<SteamCsvDto> steamCsvReader;
    private final SteamGameProcessor steamGameProcessor;
    private final JpaItemWriter<SteamGame> steamGameWriter;

    @Bean
    public Job steamGameJob() {
        return new JobBuilder("steamGameJob", jobRepository)
                .start(steamGameStep())
                .build();
    }

    @Bean
    public Step steamGameStep() {
        return new StepBuilder("steamGameStep", jobRepository)
                .<SteamCsvDto, SteamGame>chunk(500, transactionManager)
                .reader(steamCsvReader)
                .processor(steamGameProcessor)
                .writer(steamGameWriter)
                .build();
    }
}