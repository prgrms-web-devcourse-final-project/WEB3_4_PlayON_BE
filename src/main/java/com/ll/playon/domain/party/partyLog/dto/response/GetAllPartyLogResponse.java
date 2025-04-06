package com.ll.playon.domain.party.partyLog.dto.response;

import java.util.List;

public record GetAllPartyLogResponse(
        List<GetPartyLogResponse> partyLogs
) {
}
