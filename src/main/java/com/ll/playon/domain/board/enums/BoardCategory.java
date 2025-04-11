package com.ll.playon.domain.board.enums;

public enum BoardCategory {
    DAILY("일상"),
    HUMOR("유머"),
    GAME_RECOMMEND("게임추천"),
    GAME_NEWS("게임소식"),
    QUESTION("질문"),
    PARTY_RECRUIT("파티모집");

    private final String value;

    BoardCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
