package com.ll.playon.global.initData;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;
import com.ll.playon.domain.game.game.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Profile("test")
public class TestInitData {

    private final GameRepository gameRepository;

    @Autowired
    @Lazy
    private TestInitData self;

    @Bean
    public ApplicationRunner testInitDataApplicationRunner() {
        return args -> {
            self.makeSampleGames();
        };
    }

    @Transactional
    public void makeSampleGames() {
        if(gameRepository.count() > 0) return;

        SteamGenre strategy = SteamGenre.builder().name("Strategy").build();
        SteamGenre moba = SteamGenre.builder().name("MOBA").build();
        SteamGenre action = SteamGenre.builder().name("Action").build();
        SteamGenre fps = SteamGenre.builder().name("FPS").build();

        SteamGame game1 = SteamGame.builder()
                .appid(730L)
                .name("CS2")
                .headerImage("a.png")
                .genres(List.of(action, fps))
                .build();
        action.getGames().add(game1);
        fps.getGames().add(game1);

        SteamGame game2 = SteamGame.builder()
                .appid(570L)
                .name("Dota 2")
                .headerImage("a.png")
                .genres(List.of(strategy, moba))
                .build();
        strategy.getGames().add(game2);
        moba.getGames().add(game2);

        gameRepository.saveAll(List.of(game1, game2));
    }
}