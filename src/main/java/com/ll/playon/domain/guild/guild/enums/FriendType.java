package com.ll.playon.domain.guild.guild.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendType {
    FRIENDLY("친목환영"),
    FOCUSED("게임만 하고 싶어요"),
    NO_CHAT("대화 안 함");

    private final String label;
}
