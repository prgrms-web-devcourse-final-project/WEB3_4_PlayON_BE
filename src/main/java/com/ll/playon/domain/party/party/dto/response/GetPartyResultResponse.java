package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.partyLog.dto.response.GetAllPartyLogResponse;

public record GetPartyResultResponse(
        GetCompletedPartyDto partyDetail,

        GetAllPartyLogResponse partyLogs
) {
}
