package com.ll.playon.domain.game.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReleaseStatus {
    RELEASED("발매"),
    UNRELEASED("출시 예정");

    private final String korean;
}

