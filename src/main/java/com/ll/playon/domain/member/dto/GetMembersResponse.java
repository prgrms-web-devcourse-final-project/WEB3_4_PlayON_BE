package com.ll.playon.domain.member.dto;

public record GetMembersResponse(
        Long steamId,
        String username,
        String profileImg
) {}