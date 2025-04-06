package com.ll.playon.global.openFeign.dto.gameDetail;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class SteamGameDetailResponse2 {
    private Map<String, GameDetailWrapper2> games = new HashMap<>();

    @JsonAnySetter
    public void addGame(String key, GameDetailWrapper2 value) {
        games.put(key, value);
    }
}