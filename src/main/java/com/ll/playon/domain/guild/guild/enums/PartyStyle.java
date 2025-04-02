package com.ll.playon.domain.guild.guild.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyStyle {
    CASUAL("맛보기"),
    FUN("캐주얼"),
    NORMAL("노멀"),
    HARD("하드"),
    EXTREME("익스트림"),
    CHALLENGE("도전 과제"),
    SPEEDRUN("스피드러너");

    private final String label;
}
