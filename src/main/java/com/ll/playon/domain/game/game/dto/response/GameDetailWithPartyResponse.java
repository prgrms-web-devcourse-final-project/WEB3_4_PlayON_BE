package com.ll.playon.domain.game.game.dto.response;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;

import java.util.List;

public record GameDetailWithPartyResponse(
        GameDetailResponse game,
        List<PartySummaryResponse> partyList,
        List<PartyLogSummaryResponse> partyLogList
) {
    public static GameDetailWithPartyResponse from(
            SteamGame game,
            List<Party> parties,
            List<PartyLog> partyLogs
    ) {
        return new GameDetailWithPartyResponse(
                GameDetailResponse.from(game),
                parties.stream().map(PartySummaryResponse::from).toList(),
                partyLogs.stream().map(PartyLogSummaryResponse::from).toList()
        );
    }
}

