package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.dto.PartyDetailMemberDto;
import java.util.List;
import lombok.NonNull;

public record GetAllPendingMemberResponse(
        @NonNull
        List<PartyDetailMemberDto> partyMembers
) {
}
