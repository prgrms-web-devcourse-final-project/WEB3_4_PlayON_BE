package com.ll.playon.domain.guild.guildMember.service;

import com.ll.playon.domain.guild.guild.dto.response.GetGuildListResponse;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardCommentRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardLikeRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardRepository;
import com.ll.playon.domain.guild.guildMember.dto.request.*;
import com.ll.playon.domain.guild.guildMember.dto.response.GuildInfoResponse;
import com.ll.playon.domain.guild.guildMember.dto.response.GuildMemberResponse;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.guild.util.GuildPermissionValidator;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuildMemberService {

    private final GuildRepository guildRepository;
    private final MemberRepository memberRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final GuildBoardRepository guildBoardRepository;
    private final GuildBoardCommentRepository guildBoardCommentRepository;
    private final GuildBoardLikeRepository guildBoardLikeRepository;

    @Transactional(readOnly = true)
    public GuildInfoResponse getGuildInfo(Long guildId, Member actor) {
        Guild guild = getGuild(guildId);
        List<GuildMember> members = guildMemberRepository.findAllByGuild(guild);
        GuildPermissionValidator.checkManagerAccess(members, actor);

        return GuildInfoResponse.from(guild, members, members.size());
    }

    @Transactional(readOnly = true)
    public List<GuildMemberResponse> getAllGuildMembers(Long guildId, Member actor) {
        Guild guild = getGuild(guildId);
        List<GuildMember> members = guildMemberRepository.findAllByGuild(guild);
        GuildPermissionValidator.checkManagerAccess(members, actor);

        return members.stream()
                .map(gm -> {
                    int postCount = guildBoardRepository.countByAuthor(gm);
                    return GuildMemberResponse.from(gm, postCount);
                })
                .toList();
    }

    @Transactional
    public void assignManagerRole(Long guildId, Member actor, AssignManagerRequest request) {
        validateLeader(guildId, actor);
        GuildMember targetMember = getGuildMember(guildId, request.targetMemberId());

        if (targetMember.getGuildRole() == GuildRole.MANAGER) {
            throw ErrorCode.ALREADY_MANAGER.throwServiceException();
        }

        targetMember.setGuildRole(GuildRole.MANAGER);
    }

    @Transactional
    public void revokeManagerRole(Long guildId, Member actor, RevokeManagerRequest request) {
        validateLeader(guildId, actor);
        GuildMember targetMember = getGuildMember(guildId, request.targetMemberId());

        if (targetMember.getGuildRole() != GuildRole.MANAGER) {
            throw ErrorCode.NOT_MANAGER.throwServiceException();
        }

        targetMember.setGuildRole(GuildRole.MEMBER);
    }

    @Transactional
    public void inviteMember(Long guildId, Member actor, InviteMemberRequest request) {
        Guild guild = getGuild(guildId);
        validateManagerOrLeader(guildId, actor);

        Member target = memberRepository.findByUsername(request.username())
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwServiceException);

        if (guildMemberRepository.existsByGuildAndMember(guild, target)) {
            throw ErrorCode.ALREADY_GUILD_MEMBER.throwServiceException();
        }

        GuildMember newGuildMember = GuildMember.builder()
                .guild(guild)
                .member(target)
                .guildRole(GuildRole.MEMBER)
                .build();

        guildMemberRepository.save(newGuildMember);
    }

    @Transactional
    public void expelMember(Long guildId, Member actor, ExpelMemberRequest request) {
        validateManagerOrLeader(guildId, actor);
        GuildMember targetMember = getGuildMember(guildId, request.targetMemberId());

        if (targetMember.getGuildRole() == GuildRole.LEADER) {
            throw ErrorCode.CANNOT_EXPEL_LEADER.throwServiceException();
        }

        if (targetMember.getGuildRole() == GuildRole.MANAGER) {
            targetMember.setGuildRole(GuildRole.MEMBER);
        }

        deleteMemberData(targetMember);
    }

    @Transactional
    public void leaveGuild(Long guildId, Member actor, LeaveGuildRequest request) {
        Guild guild = getGuild(guildId);
        GuildMember actorMember = getGuildMember(guildId, actor.getId());

        if (actorMember.getGuildRole() == GuildRole.LEADER) {
            validateLeaderDelegation(guildId, request);
        }

        deleteMemberData(actorMember);
    }

    @Transactional(readOnly = true)
    public List<GetGuildListResponse> getMyGuilds(Member actor) {
        return guildMemberRepository.findByMember(actor).stream()
                .map(gm -> GetGuildListResponse.from(gm.getGuild()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetGuildListResponse> getMemberGuilds(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwServiceException);

        return guildMemberRepository.findByMemberIdAndGuild_IsPublicTrue(memberId).stream()
                .map(gm -> GetGuildListResponse.from(gm.getGuild()))
                .toList();
    }

    private void deleteMemberData(GuildMember member) {
        guildBoardLikeRepository.deleteByGuildMember(member);
        guildBoardCommentRepository.deleteByAuthor(member);
        guildBoardRepository.deleteByAuthor(member);
        guildMemberRepository.delete(member);
    }

    private void validateLeader(Long guildId, Member actor) {
        GuildMember actorMember = getGuildMember(guildId, actor.getId());
        GuildPermissionValidator.checkLeader(actorMember);
    }

    private void validateManagerOrLeader(Long guildId, Member actor) {
        GuildMember actorMember = getGuildMember(guildId, actor.getId());
        GuildPermissionValidator.checkManagerOrLeader(actorMember);
    }

    private void validateLeaderDelegation(Long guildId, LeaveGuildRequest request) {
        if (request == null || request.newLeaderId() == null) {
            throw ErrorCode.GUILD_LEADER_CANNOT_LEAVE.throwServiceException();
        }

        Member newLeader = getMember(request.newLeaderId());
        GuildMember delegateTarget = getGuildMember(guildId, newLeader.getId());

        if (delegateTarget.getGuildRole() != GuildRole.MANAGER) {
            throw ErrorCode.DELEGATE_MUST_BE_MANAGER.throwServiceException();
        }

        long managerCount = guildMemberRepository.findAllByGuild(getGuild(guildId)).stream()
                .filter(gm -> gm.getGuildRole() == GuildRole.MANAGER)
                .count();

        if (managerCount <= 1) {
            throw ErrorCode.CANNOT_DELEGATE_TO_SINGLE_MANAGER.throwServiceException();
        }

        delegateTarget.setGuildRole(GuildRole.LEADER);
    }

    private Guild getGuild(Long id) {
        return guildRepository.findById(id)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwServiceException);
    }

    private GuildMember getGuildMember(Long guildId, Long memberId) {
        return guildMemberRepository.findByGuildIdAndMemberId(guildId, memberId)
                .orElseThrow(ErrorCode.GUILD_MEMBER_NOT_FOUND::throwServiceException);
    }
}
