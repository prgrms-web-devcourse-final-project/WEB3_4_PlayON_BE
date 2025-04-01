package com.ll.playon.domain.party.party.dto.response;

import com.ll.playon.domain.party.party.entity.Party;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.NonNull;

// TODO : 생성 응답 데이터 확인
public record PostPartyResponse(
        long id,

        long gameId,

        @NotBlank
        String name,

        @NonNull
        String description,

        boolean isPublic,

        int minimum,

        int maximum,

        @NonNull
        List<PostPartyTagResponse> tags
) {
    public PostPartyResponse(Party party) {
        this(
                party.getId(),
                party.getGame(),
                party.getName(),
                party.getDescription(),
                party.isPublic(),
                party.getMinimum(),
                party.getMaximum(),
                PostPartyTagResponse.fromList(party.getPartyTags())
        );
    }
}
