package com.ll.playon.domain.guild.guild.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GuildTagRequest(
    @NotBlank(message = "길드 태그 타입을 입력해주세요.")
    String type,

    @NotBlank(message = "길드 태그 값을 입력해주세요.")
    String value
) {
}
