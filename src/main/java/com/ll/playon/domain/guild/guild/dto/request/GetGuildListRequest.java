package com.ll.playon.domain.guild.guild.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record GetGuildListRequest(
        String name,
        List<Long> appids,
        @NotNull
        List<@Valid GuildTagRequest> tags
) {
        public GetGuildListRequest {
                tags = Objects.requireNonNullElse(tags, new ArrayList<>());
        }

        public Map<String, List<String>> getTagMap() {
                return tags.stream()
                        .collect(Collectors.groupingBy(
                                GuildTagRequest::type,
                                Collectors.mapping(GuildTagRequest::value, Collectors.toList())
                        ));
        }
}