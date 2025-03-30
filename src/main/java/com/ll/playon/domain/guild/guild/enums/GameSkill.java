package com.ll.playon.domain.guild.guild.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameSkill {
    HIGH("고수"),
    MID("중수"),
    LOW("하수"),
    PURE("청정수"),
    NEWBIE("처음 킴?");

    private final String label;
}
