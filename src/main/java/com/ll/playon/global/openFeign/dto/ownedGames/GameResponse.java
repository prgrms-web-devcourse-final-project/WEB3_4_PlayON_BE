package com.ll.playon.global.openFeign.dto.ownedGames;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameResponse {
    @JsonProperty("games")
    private List<Game> games;

    @JsonProperty("game_count")
    private int gameCount;
}