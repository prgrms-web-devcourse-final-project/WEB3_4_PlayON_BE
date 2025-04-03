package com.ll.playon.domain.game.game.dto;

import java.util.List;

public record GameListResponse(
        Long appid,
        String name,
        String headerImage,
        List<String> gameGenres
) {
}
