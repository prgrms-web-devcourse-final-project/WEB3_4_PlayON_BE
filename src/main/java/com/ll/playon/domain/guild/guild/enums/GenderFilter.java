package com.ll.playon.domain.guild.guild.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderFilter {
    ALL("전체"),
    MALE_ONLY("남자만"),
    FEMALE_ONLY("여자만");

    private final String label;
}
