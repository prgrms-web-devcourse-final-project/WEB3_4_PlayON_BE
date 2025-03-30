package com.ll.playon.domain.guild.guild.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyStyle {
    HARDCORE("빡겜"),
    CASUAL("즐겜"),
    SPEEDRUN("스피드런"),
    COLLECTOR("콜렉터");

    private final String label;
}
