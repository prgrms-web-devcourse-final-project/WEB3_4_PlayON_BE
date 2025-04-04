package com.ll.playon.domain.game.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GameListResponse{
    private final Long appid;
    private final String name;
    private final String headerImage;
    private final List<String> gameGenres;
}
