package com.ll.playon.domain.guild.guild.service;

import com.ll.playon.domain.guild.guild.dto.*;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.MemberService;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;
    private final UserContext userContext;
    private final GuildMemberRepository guildMemberRepository;
    private final MemberService memberService;

    public PostGuildResponse createGuild(PostGuildRequest request) {
        Member owner = userContext.getActor();

        if (guildRepository.existsByName(request.name())) {
            ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }

        Guild guild = Guild.builder()
                .owner(owner)
                .name(request.name())
                .description(request.description())
                .maxMembers(request.maxMembers())
                .isPublic(request.isPublic())
                .game(request.gameId())
                .guildImg(request.guildImg())
                .partyStyle(request.partyStyle())
                .gameSkill(request.gameSkill())
                .gender(request.gender())
                .friendType(request.friendType())
                .activeTime(request.activeTime())
                .build();

        guildRepository.save(guild);

        GuildMember guildMember = GuildMember.builder()
                .guild(guild)
                .member(owner)
                .guildRole(GuildRole.LEADER)
                .build();

        guildMemberRepository.save(guildMember);

        return new PostGuildResponse(GuildDto.from(guild));
    }

    @Transactional
    public PutGuildResponse updateGuild(Long guildId, PutGuildRequest request) {
        Member actor = userContext.getActor();

        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);


       GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElseThrow(ErrorCode.GUILD_NO_PERMISSION::throwServiceException);

       if(!isManager(guildMember.getGuildRole())) {
           throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
       }

        if (!guild.getName().equals(request.name()) &&
                guildRepository.existsByName(request.name())) {
            throw ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }

       guild.setName(request.name());
       guild.setDescription(request.description());
       guild.setMaxMembers(request.maxMembers());
       guild.setPublic(request.isPublic());
       guild.setGuildImg(request.guildImg());
       guild.setPartyStyle(request.partyStyle());
       guild.setGameSkill(request.gameSkill());
       guild.setGender(request.gender());
       guild.setFriendType(request.friendType());
       guild.setActiveTime(request.activeTime());

        return new PutGuildResponse(GuildDto.from(guild));
    }

    private boolean isManager(GuildRole role) {
        return role == GuildRole.LEADER || role == GuildRole.MANAGER;
    }

    @Transactional
    public void deleteGuild(Long guildId) {
        Member actor = userContext.getActor();

        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);

        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElseThrow(ErrorCode.GUILD_NO_PERMISSION::throwServiceException);

        if (guildMember.getGuildRole() != GuildRole.LEADER) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        long memberCount = guildMemberRepository.countByGuildId(guildId);
        if(memberCount > 1) {
            throw ErrorCode.GUILD_DELETE_NOT_ALLOWED.throwServiceException();
        }

        guild.softDelete();
    }
}
