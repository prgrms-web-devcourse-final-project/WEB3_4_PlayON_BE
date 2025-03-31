package com.ll.playon.domain.guild.guildJoinRequest.service;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildJoinRequest.entity.GuildJoinRequest;
import com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState;
import com.ll.playon.domain.guild.guildJoinRequest.repository.GuildJoinRequestRepository;
import com.ll.playon.domain.member.MemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildJoinRequestService {

    private final GuildRepository guildRepository;
    private final MemberRepository memberRepository;
    private final GuildJoinRequestRepository guildJoinRequestRepository;

    @Transactional
    public void requestToJoinGuild(Long guildId, Long memberId) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> ErrorCode.PAGE_NOT_FOUND.throwServiceException());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> ErrorCode.PAGE_NOT_FOUND.throwServiceException());

        boolean alreadyRequested = guildJoinRequestRepository
                .existsByGuildAndMemberAndApprovalState(guild, member, ApprovalState.PENDING);

        if (alreadyRequested) {
            throw ErrorCode.GUILD_ALREADY_REQUESTED.throwServiceException();
        }

        GuildJoinRequest request = GuildJoinRequest.builder()
                .guild(guild)
                .member(member)
                .approvalState(ApprovalState.PENDING)
                .build();

        guildJoinRequestRepository.save(request);
    }
}

