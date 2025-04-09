package com.ll.playon.domain.guild.guild.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public record PutGuildRequest(
        @NotBlank(message = "길드 이름은 필수입니다.")
        @Size(max = 50, message = "길드 이름은 최대 50자까지 가능합니다.")
        String name,

        @NotBlank(message = "길드 소개는 필수입니다.")
        @Size(max = 500, message = "길드 소개는 최대 500자까지 가능합니다.")
        String description,

        @Min(value = 1, message = "최소 인원은 1명 이상이어야 합니다.")
        @Max(value = 100, message = "최대 인원은 100명 이하로 설정해주세요.")
        int maxMembers,

        Long appid,

        boolean isPublic,

        String newFileType,

        @NotNull(message = "태그 정보들을 입력해주세요.")
        List<GuildTagRequest> tags
) {
}
