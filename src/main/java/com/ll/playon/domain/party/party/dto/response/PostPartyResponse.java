package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.entity.Party;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.NonNull;

public record PostPartyResponse(
        long id,

        long gameId,

        @NotBlank
        String name,

        @NonNull
        String description,

        @NonNull
        String headerImage,

        boolean isPublic,

        int minimum,

        int maximum,

        @NonNull
        List<PartyTagResponse> tags
) {
    public PostPartyResponse(Party party) {
        this(
                party.getId(),
                party.getGame().getId(),
                party.getName(),
                party.getDescription(),
                party.getGame().getHeaderImage() != null ? party.getGame().getHeaderImage() : "",
                party.isPublicFlag(),
                party.getMinimum(),
                party.getMaximum(),
                PartyTagResponse.fromList(party.getPartyTags())
        );
    }
}
