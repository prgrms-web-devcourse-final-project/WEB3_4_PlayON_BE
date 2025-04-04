package com.ll.playon.domain.party.partyLog.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record PutPartyLogRequest(
        @NotNull
        String comment,

        @NotNull
        String content,

        String deleteUrl,

        String newFileType
) {
    public PutPartyLogRequest {
        comment = Objects.requireNonNullElse(comment, "");
        content = Objects.requireNonNullElse(content, "");
    }
}
