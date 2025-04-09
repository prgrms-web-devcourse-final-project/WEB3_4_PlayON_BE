package com.ll.playon.domain.guild.util;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.global.exceptions.ErrorCode;

import java.util.List;

public class GuildPermissionValidator {

    public static void checkLeader(GuildMember member) {
        if (member.getGuildRole() != GuildRole.LEADER) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    public static void checkManagerOrLeader(GuildMember member) {
        if (!member.isManagerOrLeader()) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    public static void checkManagerAccess(List<GuildMember> guildMembers, Member member) {
        GuildMember actorMember = guildMembers.stream()
                .filter(gm -> gm.getMember().equals(member))
                .findFirst()
                .orElseThrow(ErrorCode.GUILD_NO_PERMISSION::throwServiceException);

        if (!actorMember.isManagerOrLeader()) {
            throw ErrorCode.GUILD_APPROVAL_UNAUTHORIZED.throwServiceException();
        }
    }
}