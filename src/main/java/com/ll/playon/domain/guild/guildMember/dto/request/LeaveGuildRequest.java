package com.ll.playon.domain.guild.guildMember.dto.request;

public record LeaveGuildRequest(
        Long newLeaderId // null 가능
) {}
