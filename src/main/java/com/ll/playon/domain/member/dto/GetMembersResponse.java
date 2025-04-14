package com.ll.playon.domain.member.dto;

public record GetMembersResponse(
        Long memberId,
        String username,
        String profileImg
) {}