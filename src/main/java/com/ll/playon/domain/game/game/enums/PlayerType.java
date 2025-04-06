package com.ll.playon.domain.game.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlayerType {
    SINGLE("싱글 플레이"),
    MULTI("멀티 플레이");

    private final String korean;
}

