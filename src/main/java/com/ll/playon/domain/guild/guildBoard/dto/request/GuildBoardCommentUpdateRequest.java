package com.ll.playon.domain.guild.guildBoard.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GuildBoardCommentUpdateRequest(
        @NotBlank String comment
) {
}
