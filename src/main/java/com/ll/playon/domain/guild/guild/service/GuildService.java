package com.ll.playon.domain.guild.guild.service;

import com.ll.playon.domain.guild.guild.dto.GuildMemberDto;
import com.ll.playon.domain.guild.guild.dto.request.GetGuildListRequest;
import com.ll.playon.domain.guild.guild.dto.request.GuildTagRequest;
import com.ll.playon.domain.guild.guild.dto.request.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.request.PutGuildRequest;
import com.ll.playon.domain.guild.guild.dto.response.*;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.GuildTag;
import com.ll.playon.domain.guild.guild.repository.GuildMemberRepositoryCustom;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import com.ll.playon.standard.page.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final GuildMemberRepositoryCustom guildMemberRepositoryCustom;

    @Transactional
    public PostGuildResponse createGuild(PostGuildRequest request, Member owner) {
        if (guildRepository.existsByName(request.name())) {
            ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }

        // TODO: 게임 생성 후 게임 데이터로 넣기
        Guild guild = Guild.createFrom(request, owner);
        guild = guildRepository.save(guild);

        List<GuildTag> guildTags = convertTags(request.tags(), guild);
        guild.setGuildTags(guildTags);

        GuildMember guildMember = GuildMember.builder()
                .guild(guild)
                .member(owner)
                .guildRole(GuildRole.LEADER)
                .build();
        guildMemberRepository.save(guildMember);

        return PostGuildResponse.from(guild);
    }

    @Transactional
    public PutGuildResponse modifyGuild(Long guildId, PutGuildRequest request, Member actor) {
        Guild guild = getGuildOrThrow(guildId);
        GuildMember member = getGuildMemberOrThrow(guild, actor);
        validateIsManager(member);

        if (!guild.getName().equals(request.name()) &&
                guildRepository.existsByName(request.name())) {
            throw ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }

        guild.updateFromRequest(request);
        guild.getGuildTags().clear();

        List<GuildTag> guildTags = convertTags(request.tags(), guild);
        guild.getGuildTags().addAll(guildTags);
        guildRepository.save(guild);

        return PutGuildResponse.from(guild);
    }

    /**
     * 길드 상세정보 조건
     * 비공개 + 멤버 → 확인가능
     * 비공개 + 멤버X → 확인불가
     * 공개 + 멤버 → 확인가능
     * 공개 + 멤버X → 확인가능
     */
    @Transactional(readOnly = true)
    public GetGuildDetailResponse getGuildDetail(Long guildId, Member actor) {
        Guild guild = getGuildOrThrow(guildId);
        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor).orElse(null);

        if (!guild.isPublic() && guildMember == null) {
            throw ErrorCode.GUILD_NOT_FOUND.throwServiceException();
        }

        GuildRole myRole = guildMember != null ? guildMember.getGuildRole() : null;
        return GetGuildDetailResponse.from(guild, myRole);
    }

    @Transactional(readOnly = true)
    public PageDto<GuildMemberDto> getGuildMembers(Long guildId, Member actor, Pageable pageable) {
        Guild guild = getGuildOrThrow(guildId);

        boolean isPublic = guild.isPublic();
        boolean isMember = guildMemberRepository.findByGuildAndMember(guild, actor).isPresent();

        // 비공개 + 멤버X 불가
        if (!isPublic && !isMember) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
        Page<GuildMember> page = guildMemberRepositoryCustom
                .findByGuildOrderByRoleAndCreatedAt(guild, pageable);

        return new PageDto<>(page.map(GuildMemberDto::from));
    }

    @Transactional(readOnly = true)
    public PageDto<GetGuildListResponse> searchGuilds(GetGuildListRequest request) {
        Pageable pageable = PageRequest.of(request.page(), request.size());

        Page<Guild> page = guildRepository.searchGuilds(request, pageable);

        return new PageDto<>(page.map(GetGuildListResponse::from));
    }

    @Transactional(readOnly = true)
    public List<GetPopularGuildResponse> getPopularGuilds(int count) {
        // TODO: 게시판 생성 후 적용. 현재 임시로 최근 생성 순으로 정렬
        List<Guild> guilds = guildRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(PageRequest.of(0, count));

        return guilds.stream()
                .map(GetPopularGuildResponse::from)
                .toList();
    }

    @Transactional
    public void deleteGuild(Long guildId, Member actor) {
        Guild guild = getGuildOrThrow(guildId);
        GuildMember member = getGuildMemberOrThrow(guild, actor);

        // 길드장만 삭제가능
        if (member.getGuildRole() != GuildRole.LEADER) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        guild.softDelete();
    }

    // 요청으로부터 태그 리스트 생성
    private List<GuildTag> convertTags(List<GuildTagRequest> tagRequests, Guild guild) {
        return tagRequests.stream()
                .map(tag -> GuildTag.builder()
                        .guild(guild)
                        .type(TagType.fromValue(tag.type()))
                        .value(TagValue.fromValue(tag.value()))
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Guild getGuildOrThrow(Long guildId) {
        return guildRepository.findByIdAndIsDeletedFalse(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);
    }

    private GuildMember getGuildMemberOrThrow(Guild guild, Member member) {
        return guildMemberRepository.findByGuildAndMember(guild, member)
                .orElseThrow(ErrorCode.GUILD_NO_PERMISSION::throwServiceException);
    }

    private void validateIsManager(GuildMember guildMember) {
        if (!guildMember.getGuildRole().isManagerOrLeader()) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }
}
