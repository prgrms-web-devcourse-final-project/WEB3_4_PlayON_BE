package com.ll.playon.domain.game.batch.writer;

import com.ll.playon.domain.game.game.entity.SteamGame;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SteamGameJpaItemWriter extends JpaItemWriter<SteamGame> {

    private final EntityManagerFactory emf;

    @PostConstruct
    public void init() {
        setEntityManagerFactory(emf);
    }

    @Override
    public void write(Chunk<? extends SteamGame> items) {
        super.write(items);
        emf.createEntityManager().clear();
    }
}

