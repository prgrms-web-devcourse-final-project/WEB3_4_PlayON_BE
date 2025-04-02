package com.ll.playon.domain.party.party.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyRole {
    OWNER("생성자"),
    MEMBER("참여자"),
    PENDING("신청자");

    private final String value;
}
