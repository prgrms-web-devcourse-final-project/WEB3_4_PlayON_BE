package com.ll.playon.domain.guild.guildMember.service;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.dto.request.AssignManagerRequest;
import com.ll.playon.domain.guild.guildMember.dto.request.ExpelMemberRequest;
import com.ll.playon.domain.guild.guildMember.dto.request.LeaveGuildRequest;
import com.ll.playon.domain.guild.guildMember.dto.request.RevokeManagerRequest;
import com.ll.playon.domain.guild.guildMember.dto.response.GuildMemberResponse;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.security.UserContext;
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
    private final UserContext userContext;

    @Transactional(readOnly = true)
    public List<GuildMemberResponse> getAllGuildMembers(Long guildId) {
        Guild guild = getGuild(guildId);
        Member actor = userContext.getActor();

        validateManagerAccess(guild, actor);

        return guildMemberRepository.findAllByGuild(guild).stream()
                .map(GuildMemberResponse::from)
                    // TODO: 게시판 기능이 추가되면 실제 게시글 수를 여기에 조회해서 넘기기
                .toList();
    }

    @Transactional
    public void leaveGuild(Long guildId, LeaveGuildRequest request) {
        Guild guild = getGuild(guildId);
        Member actor = userContext.getActor();

        GuildMember actorMember = getGuildMember(guild, actor);

        if (actorMember.getGuildRole() == GuildRole.LEADER) {
            if (request == null || request.newLeaderId() == null) {
                throw ErrorCode.GUILD_LEADER_CANNOT_LEAVE.throwServiceException();
            }
            Member newLeader = memberRepository.findById(request.newLeaderId())
                    .orElseThrow(() -> ErrorCode.MEMBER_NOT_FOUND.throwServiceException());
            GuildMember delegateTarget = getGuildMember(guild, newLeader);

            if (delegateTarget.getGuildRole() != GuildRole.MANAGER) {
                throw ErrorCode.DELEGATE_MUST_BE_MANAGER.throwServiceException();
            }
            delegateTarget.setGuildRole(GuildRole.LEADER);
        }

        guildMemberRepository.delete(actorMember);
    }

    @Transactional
    public void assignManagerRole(Long guildId, AssignManagerRequest request) {
        Guild guild = getGuild(guildId);
        Member actor = userContext.getActor();
        Member target = getMember(request.targetMemberId());

        GuildMember actorMember = getGuildMember(guild, actor);
        validateOnlyLeader(actorMember);

        GuildMember targetMember = getGuildMember(guild, target);
        if (targetMember.getGuildRole() == GuildRole.MANAGER) {
            throw ErrorCode.ALREADY_MANAGER.throwServiceException();
        }

        targetMember.setGuildRole(GuildRole.MANAGER);
    }

    @Transactional
    public void revokeManagerRole(Long guildId, RevokeManagerRequest request) {
        Guild guild = getGuild(guildId);
        Member actor = userContext.getActor();
        Member target = getMember(request.targetMemberId());

        GuildMember actorMember = getGuildMember(guild, actor);
        validateOnlyLeader(actorMember);

        GuildMember targetMember = getGuildMember(guild, target);
        if (targetMember.getGuildRole() != GuildRole.MANAGER) {
            throw ErrorCode.NOT_MANAGER.throwServiceException();
        }

        targetMember.setGuildRole(GuildRole.MEMBER);
    }

    @Transactional
    public void expelMember(Long guildId, ExpelMemberRequest request) {
        Guild guild = getGuild(guildId);
        Member actor = userContext.getActor();
        Member target = getMember(request.targetMemberId());

        GuildMember actorMember = getGuildMember(guild, actor);
        if (!isManager(actorMember)) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        GuildMember targetMember = getGuildMember(guild, target);
        if (targetMember.getGuildRole() == GuildRole.LEADER) {
            throw ErrorCode.CANNOT_EXPEL_LEADER.throwServiceException();
        }

        if (targetMember.getGuildRole() == GuildRole.MANAGER) {
            targetMember.setGuildRole(GuildRole.MEMBER);
        }

        guildMemberRepository.delete(targetMember);
    }

    //닉네임을 기반으로 길드에 멤버 초대
    //memberRepository에 Optional<Member> findByUsername(String username) 추가
//    @Transactional
//    public void inviteMember(InviteMemberRequest request) {
//        Member requester = userContext.getActor();
//
//        Guild guild = guildRepository.findById(request.guildId())
//                .orElseThrow(() -> ErrorCode.GUILD_NOT_FOUND.throwServiceException());
//
//        GuildMember requesterMember = guildMemberRepository.findByGuildAndMember(guild, requester)
//                .orElseThrow(() -> ErrorCode.GUILD_MEMBER_NOT_FOUND.throwServiceException());
//
//        if (requesterMember.getGuildRole() != GuildRole.LEADER && requesterMember.getGuildRole() != GuildRole.MANAGER) {
//            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
//        }
//
//        Member target = memberRepository.findByUsername(request.nickname())
//                .orElseThrow(() -> ErrorCode.MEMBER_NOT_FOUND.throwServiceException());
//
//        boolean alreadyInGuild = guild.getMembers().stream()
//                .anyMatch(gm -> gm.getMember().equals(target));
//        if (alreadyInGuild) {
//            throw ErrorCode.ALREADY_GUILD_MEMBER.throwServiceException();
//        }
//
//        GuildMember newGuildMember = GuildMember.builder()
//                .guild(guild)
//                .member(target)
//                .guildRole(GuildRole.MEMBER)
//                .build();
//
//        guildMemberRepository.save(newGuildMember);
//    }

    private Guild getGuild(Long id) {
        return guildRepository.findById(id)
                .orElseThrow(() -> ErrorCode.GUILD_NOT_FOUND.throwServiceException());
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> ErrorCode.MEMBER_NOT_FOUND.throwServiceException());
    }

    private GuildMember getGuildMember(Guild guild, Member member) {
        return guildMemberRepository.findByGuildAndMember(guild, member)
                .orElseThrow(() -> ErrorCode.GUILD_MEMBER_NOT_FOUND.throwServiceException());
    }

    private void validateOnlyLeader(GuildMember gm) {
        if (gm.getGuildRole() != GuildRole.LEADER) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    private void validateManagerAccess(Guild guild, Member member) {
        boolean authorized = guild.getMembers().stream()
                .anyMatch(gm -> gm.getMember().equals(member)
                        && (gm.getGuildRole() == GuildRole.LEADER || gm.getGuildRole() == GuildRole.MANAGER));

        if (!authorized) {
            throw ErrorCode.GUILD_APPROVAL_UNAUTHORIZED.throwServiceException();
        }
    }

    private boolean isManager(GuildMember gm) {
        return gm.getGuildRole() == GuildRole.LEADER || gm.getGuildRole() == GuildRole.MANAGER;
    }
}