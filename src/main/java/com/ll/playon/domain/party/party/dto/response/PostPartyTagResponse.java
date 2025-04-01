package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.entity.PartyTag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PostPartyTagResponse(
        @NotBlank
        String type,

        @NotBlank
        String value
) {
    public PostPartyTagResponse(PartyTag partyTag) {
        this(partyTag.getType().getValue(), partyTag.getValue().getKoreanValue());
    }

    public static List<PostPartyTagResponse> fromList(List<PartyTag> partyTags) {
        return partyTags.stream()
                .map(PostPartyTagResponse::new)
                .toList();
    }
}
