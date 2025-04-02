package com.ll.playon.domain.member.dto;

import java.util.List;

public record MemberProfileResponse(
        ProfileMemberDetailDto memberDetail,
        List<GameDetailDto> ownedGames
) {}