package com.ll.playon.domain.member.dto;

import com.ll.playon.domain.game.game.dto.GameListResponse;

import java.util.List;

public record MemberProfileResponse(
        ProfileMemberDetailDto memberDetail,
        List<GameListResponse> ownedGames
) {}