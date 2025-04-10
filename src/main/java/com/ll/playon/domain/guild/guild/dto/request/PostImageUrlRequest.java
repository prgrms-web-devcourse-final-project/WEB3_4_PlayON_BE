package com.ll.playon.domain.guild.guild.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostImageUrlRequest(
        @NotBlank(message = "이미지 URL 필수입니다.")
        String url
) {
}
