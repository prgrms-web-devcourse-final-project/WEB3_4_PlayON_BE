package com.ll.playon.domain.game.game.batch.writer;

import com.ll.playon.domain.game.game.entity.SteamGame;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SteamGameWriterConfig {

    @Bean
    public JpaItemWriter<SteamGame> steamGameWriter(EntityManagerFactory emf) {
        JpaItemWriter<SteamGame> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }
}
