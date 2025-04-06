package com.ll.playon.domain.game.game.dto.response;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.entity.SteamGenre;

import java.util.List;

public record GameSummaryResponse(
        Long appid,
        String name,
        String headerImage,
        List<String> genres
) {
    public static GameSummaryResponse from(SteamGame game) {
        return new GameSummaryResponse(
                game.getAppid(),
                game.getName(),
                game.getHeaderImage(),
                game.getGenres().stream()
                        .map(SteamGenre::getName)
                        .limit(2)
                        .toList()
        );
    }
}
