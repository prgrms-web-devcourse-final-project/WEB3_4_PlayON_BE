package com.ll.playon.global.openFeign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SteamResponse {
    @JsonProperty("response")
    private PlayerResponse response;
}

