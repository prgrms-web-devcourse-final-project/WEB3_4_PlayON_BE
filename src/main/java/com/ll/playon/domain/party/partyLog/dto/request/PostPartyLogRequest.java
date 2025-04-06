package com.ll.playon.domain.party.partyLog.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record PostPartyLogRequest(
        @NotNull
        String comment,

        @NotNull
        String content,

        String fileType,

        Long partyMemberId
) {
    public PostPartyLogRequest {
        comment = Objects.requireNonNullElse(comment, "");
        content = Objects.requireNonNullElse(content, "");
    }
}
