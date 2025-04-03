package com.ll.playon.domain.party.party.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record GetAllPartiesRequest(

//        // TODO: 수정할 가능성
//        @NotNull
//        String gameName,

        // TODO: 추후 게임 작업 완료되면 진행
//        @NotNull
//        List<String> genres,

        @NotNull
        List<@Valid PartyTagRequest> tags
) {
    public GetAllPartiesRequest {
//        gameName = Objects.requireNonNullElse(gameName, "");
//        genres = Objects.requireNonNullElse(genres, new ArrayList<>());
        tags = Objects.requireNonNullElse(tags, new ArrayList<>());
    }
}
