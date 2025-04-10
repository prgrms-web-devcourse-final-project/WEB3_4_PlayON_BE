package com.ll.playon.domain.title.dto;

import lombok.NonNull;

public record RepresentativeTitleDto(
        long memberId,

        @NonNull
        String title
) {
}
