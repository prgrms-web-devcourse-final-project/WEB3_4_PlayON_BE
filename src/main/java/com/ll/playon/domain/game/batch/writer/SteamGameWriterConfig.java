package com.ll.playon.domain.game.batch.writer;

import com.ll.playon.domain.game.game.entity.SteamGame;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SteamGameWriterConfig {

    @Bean
    public ItemWriter<SteamGame> steamGameWriter(SteamGameJpaItemWriter writer) {
        return writer;
    }
}
