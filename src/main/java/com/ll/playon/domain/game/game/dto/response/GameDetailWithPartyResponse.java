package com.ll.playon.domain.game.game.dto.response;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.image.entity.Image;
import com.ll.playon.domain.image.repository.ImageRepository;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.domain.party.party.dto.response.GetPartyResponse;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.partyLog.dto.response.GetPartyLogResponse;
import com.ll.playon.domain.party.partyLog.entity.PartyLog;

import java.util.List;

public record GameDetailWithPartyResponse(
        GameDetailResponse game,
        List<GetPartyResponse> partyList,
        List<GetPartyLogResponse> partyLogList
) {
    public static GameDetailWithPartyResponse from(
            SteamGame game,
            List<Party> parties,
            List<PartyLog> partyLogs,
            ImageRepository imageRepository
    ) {
        return new GameDetailWithPartyResponse(
                GameDetailResponse.from(game),
                parties.stream()
                        .map(party -> {
                            List<PartyTag> tags = party.getPartyTags();
                            List<PartyMember> members = party.getPartyMembers();
                            return new GetPartyResponse(party, tags, members);
                        })
                        .toList(),
                partyLogs.stream()
                        .map(partyLog -> {
                            Image screenshot = imageRepository.findByImageTypeAndReferenceId(
                                    ImageType.LOG,
                                    partyLog.getId()
                            );
                            String screenshotUrl = screenshot != null ? screenshot.getImageUrl() : null;
                            return new GetPartyLogResponse(partyLog, screenshotUrl);
                        })
                        .toList()
        );
    }
}