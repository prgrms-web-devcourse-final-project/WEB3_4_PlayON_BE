package com.ll.playon.domain.member.dto;

import java.util.List;

public record GameDetailDto(
        Long appId,
        String name,
        String img,
        List<String> genres
) {}