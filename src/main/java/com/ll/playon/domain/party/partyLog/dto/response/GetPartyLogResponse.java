package com.ll.playon.domain.party.partyLog.dto.response;

import com.ll.playon.domain.party.partyLog.entity.PartyLog;
import java.util.Objects;
import lombok.NonNull;

public record GetPartyLogResponse(
        long memberId,

        @NonNull
        String nickname,

        @NonNull
        String profileImageUrl,

        @NonNull
        String comment,

        @NonNull
        String content,

        String screenShotUrl
) {
    public GetPartyLogResponse(PartyLog partyLog, String screenShotUrl) {
        this(
                partyLog.getPartyMember().getMember().getId(),
                partyLog.getPartyMember().getMember().getNickname(),
                Objects.requireNonNullElse(partyLog.getPartyMember().getMember().getProfileImg(), ""),
                partyLog.getComment(),
                partyLog.getContent(),
                screenShotUrl
        );
    }
}
