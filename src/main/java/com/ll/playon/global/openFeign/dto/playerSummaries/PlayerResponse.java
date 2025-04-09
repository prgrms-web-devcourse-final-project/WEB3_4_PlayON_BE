package com.ll.playon.global.openFeign.dto.playerSummaries;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayerResponse {
    @JsonProperty("players")
    private List<Player> players;
}
