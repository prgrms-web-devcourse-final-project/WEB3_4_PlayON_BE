package com.ll.playon.domain.title.entity;

import lombok.Builder;

import java.time.LocalDateTime;

public record TitleDto(
        Long titleId,
        String name,
        String description,
        LocalDateTime acquiredAt,
        boolean isRepresentative
) {
    @Builder
    public TitleDto {}
}
