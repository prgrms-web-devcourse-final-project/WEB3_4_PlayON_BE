package com.ll.playon.domain.guild.util;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState;
import com.ll.playon.domain.guild.guildJoinRequest.repository.GuildJoinRequestRepository;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuildJoinRequestValidator {

    private final GuildJoinRequestRepository guildJoinRequestRepository;
    private final GuildMemberRepository guildMemberRepository;

    public void validateJoinable(Guild guild, Member member) {
        if (isAlreadyMember(guild, member)) {
            throw ErrorCode.ALREADY_GUILD_MEMBER.throwServiceException();
        }

        if (isMemberLimitExceeded(guild)) {
            throw ErrorCode.GUILD_MEMBER_LIMIT_EXCEEDED.throwServiceException();
        }

        if (isAlreadyRequested(guild, member)) {
            throw ErrorCode.GUILD_ALREADY_REQUESTED.throwServiceException();
        }
    }

    private boolean isAlreadyMember(Guild guild, Member member) {
        return guild.getMembers().stream()
                .anyMatch(gm -> gm.getMember().getId().equals(member.getId()));
    }

    private boolean isMemberLimitExceeded(Guild guild) {
        int currentMembers = (int) guildMemberRepository.countByGuildId(guild.getId());
        return currentMembers >= guild.getMaxMembers();
    }

    private boolean isAlreadyRequested(Guild guild, Member member) {
        return guildJoinRequestRepository.existsByGuildAndMemberAndApprovalState(
                guild, member, ApprovalState.PENDING);
    }
}