package com.ll.playon.domain.party.party.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record GetAllPartiesRequest(
        Long gameId,

        @NotNull
        List<String> genres,

        @NotNull
        List<@Valid PartyTagRequest> tags
) {
    public GetAllPartiesRequest {
        genres = Objects.requireNonNullElse(genres, new ArrayList<>());
        tags = Objects.requireNonNullElse(tags, new ArrayList<>());
    }
}
