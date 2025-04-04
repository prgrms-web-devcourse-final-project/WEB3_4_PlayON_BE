package com.ll.playon.domain.party.partyLog.dto.response;

import java.net.URL;

public record PostPartyLogResponse(
        long logId,

        long partyId,

        URL presignedUrl
) {
}
