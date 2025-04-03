package com.ll.playon.global.openFeign.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class SteamGameDetailResponse {
    private Map<String, GameDetailWrapper> games = new HashMap<>();

    @JsonAnySetter
    public void addGame(String key, GameDetailWrapper value) {
        games.put(key, value);
    }
}