package com.ll.playon.domain.guild.guild.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActiveTime {
    ALL("전체"),
    MORNING("아침"),
    NOON("점심"),
    EVENING("저녁"),
    NIGHT("밤"),
    DAWN("새벽"),
    WEEKEND_ONLY("주말만");

    private final String label;
}
