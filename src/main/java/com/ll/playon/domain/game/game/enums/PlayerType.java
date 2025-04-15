package com.ll.playon.domain.game.game.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ll.playon.global.exceptions.ErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlayerType {
    SINGLE("싱글플레이"),
    MULTI("멀티플레이");

    private final String korean;

    @JsonCreator
    public static PlayerType fromValue(String value) {
        return Arrays.stream(values())
                .filter(playerType -> playerType.getKorean().equals(value))
                .findFirst()
                .orElseThrow(ErrorCode.PLAYER_TYPE_CONVERT_FAILED::throwServiceException);
    }
}

