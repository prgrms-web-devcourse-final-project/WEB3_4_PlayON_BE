package com.ll.playon.global.openFeign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    @JsonProperty("personaname")
    private String nickname;

    @JsonProperty("avatarmedium") // medium 사이즈 프로필 사용
    private String avatar;
}
