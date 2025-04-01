package com.ll.playon.domain.guild.guild.service;

import com.ll.playon.domain.guild.guild.dto.*;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.enums.GuildDetailDto;
import com.ll.playon.domain.guild.guild.repository.GuildMemberQueryRepository;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.standard.page.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;
    private final UserContext userContext;
    private final GuildMemberRepository guildMemberRepository;
    private final GuildMemberQueryRepository guildMemberQueryRepository;

    public PostGuildResponse createGuild(PostGuildRequest request) {
        Member owner = userContext.getActor();

        if (guildRepository.existsByName(request.name())) {
            ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }

        // TODO: 게임 생성 후 게임 데이터로 넣기
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
                .genderFilter(request.genderFilter())
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
    public PutGuildResponse modifyGuild(Long guildId, PutGuildRequest request) {
        Member actor = userContext.getActor();

        Guild guild = guildRepository.findByIdAndIsDeletedFalse(guildId)
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
       guild.setGenderFilter(request.genderFilter());
       guild.setActiveTime(request.activeTime());

        return new PutGuildResponse(GuildDto.from(guild));
    }

    private boolean isManager(GuildRole role) {
        return role == GuildRole.LEADER || role == GuildRole.MANAGER;
    }

    @Transactional
    public void deleteGuild(Long guildId) {
        Member actor = userContext.getActor();

        Guild guild = guildRepository.findByIdAndIsDeletedFalse(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);

        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElseThrow(ErrorCode.GUILD_NO_PERMISSION::throwServiceException);

        // 길드장만 삭제가능
        if (guildMember.getGuildRole() != GuildRole.LEADER) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        guild.softDelete();
    }

    /**
     * 길드 상세정보 조건
     * 비공개 + 멤버 → 확인가능
     * 비공개 + 멤버X → 확인불가
     * 공개 + 멤버 → 확인가능
     * 공개 + 멤버X → 확인가능
     */
    @Transactional(readOnly = true)
    public GuildDetailDto getGuildDetail(Long guildId) {
        Member actor = userContext.getActor();

        Guild guild = guildRepository.findByIdAndIsDeletedFalse(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);

        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElse(null);

        GuildRole myRole = guildMember != null ? guildMember.getGuildRole() : null;

        // 비공개 + 멤버 아님
        if (!guild.isPublic() && guildMember == null) {
            throw ErrorCode.GUILD_NOT_FOUND.throwServiceException();
        }

        return GuildDetailDto.from(guild, myRole);
    }

    public PageDto<GuildMemberDto> getGuildMembers(Long guildId, Pageable pageable) {
        Member actor = userContext.getActor();

        Guild guild = guildRepository.findByIdAndIsDeletedFalse(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);

        guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);

        Page<GuildMember> page = guildMemberQueryRepository
                .findByGuildOrderByRoleAndCreatedAt(guild, pageable);

        return new PageDto<>(page.map(GuildMemberDto::from));
    }

    public PageDto<GetGuildListResponse> searchGuilds(GetGuildListRequest request) {
        Pageable pageable = PageRequest.of(request.page(), request.size());

        Page<Guild> page = guildRepository.searchGuilds(request, pageable);

        return new PageDto<>(page.map(GetGuildListResponse::from));
    }
}
