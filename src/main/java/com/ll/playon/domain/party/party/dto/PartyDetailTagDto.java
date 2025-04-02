package com.ll.playon.domain.party.party.dto;

import com.ll.playon.domain.party.party.entity.PartyTag;
import lombok.NonNull;

public record PartyDetailTagDto(
        @NonNull
        String tagValue
) {
    public PartyDetailTagDto(PartyTag partyTag) {
        this(
                partyTag.getValue().getKoreanValue()
        );
    }
}
