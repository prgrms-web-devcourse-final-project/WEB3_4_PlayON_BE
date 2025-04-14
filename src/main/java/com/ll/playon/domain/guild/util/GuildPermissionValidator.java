package com.ll.playon.domain.guild.util;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;

import java.util.List;

public class GuildPermissionValidator {

    // 길드장 권한 확인
    public static void checkLeader(GuildMember member) {
        if (member.getGuildRole() != GuildRole.LEADER) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    // 운영진 권한 확인
    public static void checkManagerOrLeader(GuildMember member) {
        if (member.isNotManagerOrLeader()) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    // 비공개 길드 접근 체크
    public static void checkPublicOrMember(Guild guild, GuildMember guildMember) {
        if (!guild.isPublic() && guildMember == null) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    // 운영진으로 권한 승인 처리
    public static void checkManagerAccess(List<GuildMember> guildMembers, Member member) {
        GuildMember actorMember = guildMembers.stream()
                .filter(gm -> gm.getMember().getId().equals(member.getId()))
                .findFirst()
                .orElseThrow(ErrorCode.GUILD_NO_PERMISSION::throwServiceException);

        if (actorMember.isNotManagerOrLeader()) {
            throw ErrorCode.GUILD_APPROVAL_UNAUTHORIZED.throwServiceException();
        }
    }
}