package com.ll.playon.domain.party.party.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record PostPartyRequest(
        @NotBlank(message = "파티 룸 이름을 입력해주세요.")
        String name,

        String description,

        @NotNull(message = "파티 진행 일자를 입력해주세요.")
        LocalDateTime partyAt,

        @NotNull(message = "공개 비공개 여부를 입력해주세요.")
        Boolean isPublic,

        @NotNull(message = "최소 인원을 입력해주세요.") @Min(value = 2, message = "최소 인원은 2명부터 가능합니다.")
        Integer minimum,

        @NotNull(message = "최대 인원을 입력해주세요.") @Max(value = 50, message = "최대 인원은 50명까지 가능합니다.")
        Integer maximum,

        @NotNull
        Long appId,

        @NotNull(message = "태그 정보들을 입력해주세요.")
        List<@Valid PartyTagRequest> tags
) {
}
