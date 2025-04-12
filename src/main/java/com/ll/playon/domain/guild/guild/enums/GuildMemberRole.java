package com.ll.playon.domain.guild.guild.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GuildMemberRole {
    LEADER("길드장"),
    MANAGER("운영진"),
    MEMBER("일반 멤버"),
    APPLICANT("가입 신청자"),
    GUEST("게스트");

    private final String description;
}
