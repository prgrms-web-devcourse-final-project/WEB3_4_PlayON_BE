package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.entity.Party;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.NonNull;

public record PutPartyResponse(
        long id,

        long gameId,

        @NotBlank
        String name,

        @NonNull
        String description,

        boolean isPublic,

        int minimum,

        int maximum,

        @NonNull
        List<PartyTagResponse> tags
) {
    public PutPartyResponse(Party party) {
        this(
                party.getId(),
                party.getGame(),
                party.getName(),
                party.getDescription(),
                party.isPublic(),
                party.getMinimum(),
                party.getMaximum(),
                PartyTagResponse.fromList(party.getPartyTags())
        );
    }
}