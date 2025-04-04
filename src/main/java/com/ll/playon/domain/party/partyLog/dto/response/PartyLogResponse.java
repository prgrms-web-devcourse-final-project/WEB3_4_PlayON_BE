package com.ll.playon.domain.party.partyLog.dto.response;

import java.net.URL;

public record PartyLogResponse(
        long logId,

        long partyId,

        URL presignedUrl
) {
}
