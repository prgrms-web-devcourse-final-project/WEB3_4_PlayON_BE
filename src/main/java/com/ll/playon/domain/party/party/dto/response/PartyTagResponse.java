package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.entity.PartyTag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PartyTagResponse(
        @NotBlank
        String type,

        @NotBlank
        String value
) {
    public PartyTagResponse(PartyTag partyTag) {
        this(partyTag.getType().getValue(), partyTag.getValue().getKoreanValue());
    }

    public static List<PartyTagResponse> fromList(List<PartyTag> partyTags) {
        return partyTags.stream()
                .map(PartyTagResponse::new)
                .toList();
    }
}
