package com.ll.playon.domain.party.party.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostPartyTagRequest(
        @NotBlank(message = "파티 태그 타입을 입력해주세요.")
        String type,

        @NotBlank(message = "파티 태그 값을 입력해주세요.")
        String value
) {
}
