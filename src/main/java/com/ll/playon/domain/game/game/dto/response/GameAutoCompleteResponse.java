package com.ll.playon.domain.game.game.dto.response;

import com.ll.playon.domain.game.game.entity.SteamGame;

public record GameAutoCompleteResponse(Long appid, String name) {
    public static GameAutoCompleteResponse from(SteamGame game) {
        return new GameAutoCompleteResponse(game.getAppid(), game.getName());
    }
}
