package com.ll.playon.domain.guild.guildJoinRequest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalState {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨");

    private final String label;
}
