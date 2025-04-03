package com.ll.playon.global.openFeign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SteamGameResponse {
    @JsonProperty("response")
    private GameResponse response;
}