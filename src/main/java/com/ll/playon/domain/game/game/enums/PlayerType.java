package com.ll.playon.domain.game.game.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ll.playon.global.exceptions.ErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlayerType {
    SINGLE("싱글 플레이"),
    MULTI("멀티 플레이");

    private final String korean;

    @JsonCreator
    public static PlayerType fromValue(String value) {
        return Arrays.stream(values())
                .filter(playerType -> playerType.getKorean().equals(value))
                .findFirst()
                .orElseThrow(ErrorCode.TAG_VALUE_CONVERT_FAILED::throwServiceException);
    }
}

