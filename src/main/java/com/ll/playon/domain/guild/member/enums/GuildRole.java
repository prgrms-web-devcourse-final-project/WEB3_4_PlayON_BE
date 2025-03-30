package com.ll.playon.domain.guild.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GuildRole {
    LEADER("길드장"),
    manager("운영진"),
    MEMBER("멤버");

    private final String label;
}
