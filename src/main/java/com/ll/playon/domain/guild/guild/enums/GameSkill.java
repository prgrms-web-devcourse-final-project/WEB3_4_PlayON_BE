package com.ll.playon.domain.guild.guild.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameSkill {
    ALL("전체"),
    NEWBIE("뉴비"),
    PRO("프로"),
    HACKER("해커"),
    MASTER("마스터");

    private final String label;
}
