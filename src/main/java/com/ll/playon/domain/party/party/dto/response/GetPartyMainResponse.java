package com.ll.playon.domain.party.party.dto.response;

import java.util.List;
import lombok.NonNull;

public record GetPartyMainResponse(
        @NonNull
        List<GetPartyResponse> parties
) {
}
