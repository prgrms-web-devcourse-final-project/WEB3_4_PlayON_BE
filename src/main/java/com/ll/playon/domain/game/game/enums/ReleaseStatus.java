package com.ll.playon.domain.game.game.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ll.playon.global.exceptions.ErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReleaseStatus {
    RELEASED("발매"),
    UNRELEASED("출시 예정");

    private final String korean;

    @JsonCreator
    public static ReleaseStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(releaseStatus -> releaseStatus.getKorean().equals(value))
                .findFirst()
                .orElseThrow(ErrorCode.TAG_VALUE_CONVERT_FAILED::throwServiceException);
    }
}

