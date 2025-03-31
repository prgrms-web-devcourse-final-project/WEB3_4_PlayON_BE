package com.ll.playon.domain.guild.guildMember.service;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.dto.response.GuildMemberResponse;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.MemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuildMemberService {

    private final GuildRepository guildRepository;
    private final MemberRepository memberRepository;
    private final GuildMemberRepository guildMemberRepository;

    @Transactional(readOnly = true)
    public List<GuildMemberResponse> getAllGuildMembers(Long guildId, Long viewerId) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> ErrorCode.GUILD_NOT_FOUND.throwServiceException());

        Member viewer = memberRepository.findById(viewerId)
                .orElseThrow(() -> ErrorCode.MEMBER_NOT_FOUND.throwServiceException());

        boolean isAuthorized = guild.getMembers().stream()
                .anyMatch(gm -> gm.getMember().equals(viewer) &&
                        (gm.getGuildRole() == GuildRole.LEADER || gm.getGuildRole() == GuildRole.MANAGER));

        if (!isAuthorized) {
            throw ErrorCode.GUILD_APPROVAL_UNAUTHORIZED.throwServiceException();
        }

        List<GuildMember> guildMembers = guildMemberRepository.findAllByGuild(guild);

        return guildMembers.stream()
                .map(gm -> {
                    Member member = gm.getMember();
                    // TODO: 게시판 기능이 추가되면 실제 게시글 수를 여기에 조회해서 넘기기
                    // int postCount = guildBoardRepository.countByGuildAndMember(guild, member);
                    return GuildMemberResponse.from(gm);
                })
                .toList();
    }

    @Transactional
    public void leaveGuild(Long guildId, Long memberId, Long newLeaderId) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> ErrorCode.GUILD_NOT_FOUND.throwServiceException());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> ErrorCode.MEMBER_NOT_FOUND.throwServiceException());

        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, member)
                .orElseThrow(() -> ErrorCode.GUILD_MEMBER_NOT_FOUND.throwServiceException());

        // 길드장이 탈퇴하는 경우
        if (guildMember.getGuildRole() == GuildRole.LEADER) {
            if (newLeaderId == null) {
                throw ErrorCode.GUILD_LEADER_CANNOT_LEAVE.throwServiceException(); // 위임 대상 없으면 예외
            }

            Member newLeader = memberRepository.findById(newLeaderId)
                    .orElseThrow(() -> ErrorCode.MEMBER_NOT_FOUND.throwServiceException());

            GuildMember delegateTarget = guildMemberRepository.findByGuildAndMember(guild, newLeader)
                    .orElseThrow(() -> ErrorCode.GUILD_MEMBER_NOT_FOUND.throwServiceException());

            if (delegateTarget.getGuildRole() != GuildRole.MANAGER) {
                throw ErrorCode.DELEGATE_MUST_BE_MANAGER.throwServiceException();
            }

            delegateTarget.setGuildRole(GuildRole.LEADER);
        }

        guildMemberRepository.delete(guildMember);
    }

    @Transactional
    public void assignManagerRole(Long guildId, Long memberId, Long requesterId) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> ErrorCode.GUILD_NOT_FOUND.throwServiceException());

        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> ErrorCode.MEMBER_NOT_FOUND.throwServiceException());

        GuildMember requesterMember = guildMemberRepository.findByGuildAndMember(guild, requester)
                .orElseThrow(() -> ErrorCode.GUILD_MEMBER_NOT_FOUND.throwServiceException());

        if (requesterMember.getGuildRole() != GuildRole.LEADER) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException(); // 길드장만 부여 가능
        }

        Member target = memberRepository.findById(memberId)
                .orElseThrow(() -> ErrorCode.MEMBER_NOT_FOUND.throwServiceException());

        GuildMember targetMember = guildMemberRepository.findByGuildAndMember(guild, target)
                .orElseThrow(() -> ErrorCode.GUILD_MEMBER_NOT_FOUND.throwServiceException());

        if (targetMember.getGuildRole() == GuildRole.MANAGER) {
            throw ErrorCode.ALREADY_MANAGER.throwServiceException(); // 이미 운영진
        }

        targetMember.setGuildRole(GuildRole.MANAGER);
    }
}