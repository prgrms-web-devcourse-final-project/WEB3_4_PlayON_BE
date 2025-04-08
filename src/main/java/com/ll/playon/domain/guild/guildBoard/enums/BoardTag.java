package com.ll.playon.domain.guild.guildBoard.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BoardTag {
    NOTICE("공지"),
    FREE("자유"),
    GAME("게임관련");

    private final String displayName;

    BoardTag(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}