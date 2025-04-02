package com.ll.playon.domain.guild.guild.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Map;

public record GetGuildListRequest(
        String name,
        List<Long> gameIds,
        Map<String, List<String>> tagFilters,

        @Min(0)
        int page,

        @Min(1)
        @Max(100)
        int size,

        String sort // latest, activity, members
) {
}
