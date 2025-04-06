package com.ll.playon.domain.game.game.dto.request;

import com.ll.playon.domain.game.game.enums.PlayerType;
import com.ll.playon.domain.game.game.enums.ReleaseStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record GameSearchCondition(
        String keyword,
        Boolean isMacSupported,
        LocalDate releasedAfter,
        ReleaseStatus releaseStatus,
        PlayerType playerType,
        List<String> genres
) {}