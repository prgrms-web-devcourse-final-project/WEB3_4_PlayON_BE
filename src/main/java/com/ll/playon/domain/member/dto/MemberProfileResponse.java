package com.ll.playon.domain.member.dto;

public record MemberProfileResponse(
        ProfileMemberDetailDto memberDetail,
        MemberOwnedGamesDto ownedGames
) {}