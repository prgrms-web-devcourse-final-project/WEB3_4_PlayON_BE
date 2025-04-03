package com.ll.playon.domain.game.game.service;

import com.ll.playon.domain.game.game.dto.GameListResponse;
import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.repository.GameGenreRepository;
import com.ll.playon.domain.game.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameGenreRepository gameGenreRepository;

    public List<GameListResponse> getGameList(List<Long> appIds) {
        List<GameListResponse> responses = new ArrayList<>();

        List<SteamGame> gameList = gameRepository.findAllByAppidIn(appIds);
        for (SteamGame game : gameList) {
            List<String> genres = gameGenreRepository.findByGame(game).stream()
                    .map(gameGenre -> gameGenre.getGenre().getName()).toList();

            responses.add(new GameListResponse(game.getAppid(), game.getName(), game.getHeaderImage(), genres));
        }

        return responses;
    }
}
