package com.ll.playon.global.openFeign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameDetailWrapper {
    private boolean success;

    @JsonProperty("data")
    @JsonDeserialize(using = GameDetailDeserializer.class) // 커스텀 deserializer 적용
    private GameDetail gameData;
}
