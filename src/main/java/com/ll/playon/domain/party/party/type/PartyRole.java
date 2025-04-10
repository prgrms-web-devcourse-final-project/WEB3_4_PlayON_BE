package com.ll.playon.domain.party.party.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyRole {
    OWNER("생성자"),
    MEMBER("참여자"),
    PENDING("대기자"),
    INVITER("초대자");

    private final String value;
}
