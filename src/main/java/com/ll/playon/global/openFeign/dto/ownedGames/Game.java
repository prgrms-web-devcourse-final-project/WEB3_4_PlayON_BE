package com.ll.playon.global.openFeign.dto.ownedGames;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    @JsonProperty("appid")
    private String appId;

    @JsonProperty("playtime_forever")
    private int playtime;
}