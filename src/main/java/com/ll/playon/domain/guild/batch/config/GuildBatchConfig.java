package com.ll.playon.domain.guild.batch.config;

import com.ll.playon.domain.guild.batch.tesklet.GuildTasklet;
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
public class GuildBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final GuildTasklet guildTasklet;

    @Bean
    public Job guildBatchJob() {
        return new JobBuilder("popularGuildStep", jobRepository)
                .start(popularGuildStep())
                .build();
    }

    @Bean
    public Step popularGuildStep() {
        return new StepBuilder("popularGuildStep", jobRepository)
                .tasklet(guildTasklet, transactionManager)
                .build();
    }
}
