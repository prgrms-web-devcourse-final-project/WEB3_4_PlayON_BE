package com.ll.playon.domain.member.dto;

import java.util.List;

public record MemberOwnedGamesDto(
        List<GameDetailDto> ownedGames
) {}