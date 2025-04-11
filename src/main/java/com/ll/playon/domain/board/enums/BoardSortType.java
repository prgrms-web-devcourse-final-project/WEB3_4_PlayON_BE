package com.ll.playon.domain.board.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BoardSortType {
    LATEST("최신순"),
    POPULAR("인기순");

    private final String value;

    public static BoardSortType from(String sort) {
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(sort))
                .findFirst()
                .orElse(LATEST); // 기본 정렬
    }
}
