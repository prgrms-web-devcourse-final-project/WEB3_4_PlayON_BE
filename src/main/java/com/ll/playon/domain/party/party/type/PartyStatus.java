package com.ll.playon.domain.party.party.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyStatus {
    PENDING("대기 중"),
    ONGOING("진행 중"),
    COMPLETED("종료됨");

    private final String value;
}
