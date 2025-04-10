package com.ll.playon.domain.guild.guildJoinRequest.service;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildJoinRequest.dto.response.GuildJoinRequestResponse;
import com.ll.playon.domain.guild.guildJoinRequest.entity.GuildJoinRequest;
import com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState;
import com.ll.playon.domain.guild.guildJoinRequest.repository.GuildJoinRequestRepository;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.service.MemberTitleService;
import com.ll.playon.global.exceptions.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuildJoinRequestService {

    private final GuildRepository guildRepository;
    private final GuildJoinRequestRepository guildJoinRequestRepository;
    private final MemberTitleService memberTitleService;

    @Transactional
    public void requestToJoinGuild(Long guildId, Member member) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> ErrorCode.GUILD_NOT_FOUND.throwServiceException());

        boolean isAlreadyMember = guild.getMembers().stream()
                .anyMatch(gm -> gm.getMember().getId().equals(member.getId()));

        if (isAlreadyMember) {
            throw ErrorCode.ALREADY_GUILD_MEMBER.throwServiceException();
        }

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

    @Transactional
    public void approveJoinRequest(Long guildId, Long requestId, Member approver) {
        processJoinRequest(guildId, requestId, approver, ApprovalState.APPROVED);
    }

    @Transactional
    public void rejectJoinRequest(Long guildId, Long requestId, Member approver) {
        processJoinRequest(guildId, requestId, approver, ApprovalState.REJECTED);
    }

    private void processJoinRequest(Long guildId, Long requestId, Member approver, ApprovalState targetState) {
        GuildJoinRequest joinRequest = guildJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> ErrorCode.GUILD_JOIN_REQUEST_NOT_FOUND.throwServiceException());

        if (!joinRequest.getGuild().getId().equals(guildId)) {
            throw ErrorCode.GUILD_ID_MISMATCH.throwServiceException();
        }

        boolean isAuthorized = joinRequest.getGuild().getMembers().stream()
                .anyMatch(gm -> gm.getMember().getId().equals(approver.getId())
                        && (gm.getGuildRole() == GuildRole.LEADER || gm.getGuildRole() == GuildRole.MANAGER));

        if (!isAuthorized) {
            throw ErrorCode.GUILD_APPROVAL_UNAUTHORIZED.throwServiceException();
        }

        if (joinRequest.getApprovalState() != ApprovalState.PENDING) {
            throw ErrorCode.GUILD_REQUEST_ALREADY_PROCESSED.throwServiceException();
        }

        joinRequest.setApprovalState(targetState);
        joinRequest.setApprovedBy(approver);
    }

    @Transactional(readOnly = true)
    public List<GuildJoinRequestResponse> getPendingJoinRequests(Long guildId, Member viewer) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> ErrorCode.GUILD_NOT_FOUND.throwServiceException());

        boolean isAuthorized = guild.getMembers().stream()
                .anyMatch(gm -> gm.getMember().getId().equals(viewer.getId())
                        && (gm.getGuildRole() == GuildRole.LEADER || gm.getGuildRole() == GuildRole.MANAGER));

        if (!isAuthorized) {
            throw ErrorCode.GUILD_APPROVAL_UNAUTHORIZED.throwServiceException();
        }

        List<GuildJoinRequest> pendingRequests = guildJoinRequestRepository
                .findAllByGuildAndApprovalState(guild, ApprovalState.PENDING);

        return pendingRequests.stream()
                .map(request -> {
                    String titleName = memberTitleService.getRepresentativeTitle(request.getMember());
                    return GuildJoinRequestResponse.from(request, titleName);
                })
                .toList();
    }
}