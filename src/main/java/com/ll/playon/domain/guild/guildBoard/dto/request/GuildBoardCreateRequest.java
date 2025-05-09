package com.ll.playon.domain.guild.guildBoard.dto.request;

import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GuildBoardCreateRequest (
        @NotBlank String title,
        @NotBlank String content,
        @NotNull BoardTag tag,
        String fileType
){}
