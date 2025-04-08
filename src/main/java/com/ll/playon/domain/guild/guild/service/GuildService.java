package com.ll.playon.domain.guild.guild.service;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.repository.GameRepository;
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
import com.ll.playon.domain.image.service.ImageService;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.service.TitleEvaluator;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import com.ll.playon.standard.page.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final GuildMemberRepositoryCustom guildMemberRepositoryCustom;
    private final GameRepository gameRepository;
    private final TitleEvaluator titleEvaluator;
    private final ImageService imageService;

    @Transactional
    public PostGuildResponse createGuild(PostGuildRequest request, Member owner) {
        // 이름 중복 확인
        if (guildRepository.existsByName(request.name())) {
            ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }

        // 게임 확인
        SteamGame game = gameRepository.findByAppid(request.appid())
                .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);

        // 길드 저장
        Guild guild = guildRepository.save(Guild.createFrom(request, owner, game));

        // 태그 설정
        guild.setGuildTags(convertTags(request.tags(), guild));

        // 길드장
        GuildMember guildMember = GuildMember.builder()
                .guild(guild)
                .member(owner)
                .guildRole(GuildRole.LEADER)
                .build();
        guildMemberRepository.save(guildMember);

        // 이미지 저장
        if (StringUtils.hasText(request.guildImg())) {
            imageService.saveImage(ImageType.GUILD, guild.getId(), request.guildImg());
        }

        // 길드 생성 칭호
        titleEvaluator.check(ConditionType.GUILD_CREATE, owner);

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

        // 게임 확인
        SteamGame game = gameRepository.findByAppid(request.appid())
                .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);

        // 기존 이미지 확인 -> 없으면 ""
        // TODO: 빈 문자열보단 따로 컨트롤하는게 좋아보이는데 논의 필요
        String currentImgUrl = imageService.getImageById(ImageType.GUILD, guildId);

        // 이미지 수정
        if (StringUtils.hasText(request.guildImg()) && !request.guildImg().equals(currentImgUrl)) {
            // 기존 이미지 삭제 image, S3
            imageService.deleteImagesByIdAndUrl(ImageType.GUILD, guildId, currentImgUrl);

            // 새 이미지 저장
            imageService.saveImage(ImageType.GUILD, guildId, request.guildImg());
        }

        // 길드 수정
        guild.updateFromRequest(request, game);

        // 태그 수정
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

        boolean isMember = guildMemberRepository.findByGuildAndMember(guild, actor).isPresent();

        // 비공개 + 멤버X 불가
        if (!guild.isPublic() && !isMember) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        Page<GuildMember> page = guildMemberRepositoryCustom
                .findByGuildOrderByRoleAndCreatedAt(guild, pageable);

        return new PageDto<>(page.map(GuildMemberDto::from));
    }

    @Transactional(readOnly = true)
    public PageDto<GetGuildListResponse> searchGuilds(int page, int pageSize, String sort, GetGuildListRequest request) {

        Page<Guild> guilds = guildRepository.searchGuilds(request, PageRequest.of(page, pageSize), sort);

        return new PageDto<>(guilds.map(GetGuildListResponse::from));
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

        // 태그 삭제
        guild.getGuildTags().clear();

        // 멤버 삭제
        guild.getMembers().clear();

        // 이미지 삭제
        // TODO: 지금 임시 a.png 저장. 삭제 실패. 실제 S3 연결 후 다시 확인해 볼 것
//        String imageUrl = imageService.getImageById(ImageType.GUILD, guildId); // "" / 실제 URL
//        if (StringUtils.hasText(imageUrl)) {
//            imageService.deleteImagesByIdAndUrl(ImageType.GUILD, guildId, imageUrl);
//        }

        // 정보 마스킹
        guild.softDelete();

        guildRepository.save(guild); // 명시적으로 저장
    }

    @Transactional(readOnly = true)
    public List<GetRecommendGuildResponse> getRecommendedGuildsByGame(int count, long appid) {
        return guildRepository.findTopNByGameAppid(appid, PageRequest.of(0, count))
                .stream()
                .map(GetRecommendGuildResponse::from)
                .toList();
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
