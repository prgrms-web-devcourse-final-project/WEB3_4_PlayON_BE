package com.ll.playon.domain.guild.guild.service;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.guild.guild.dto.request.*;
import com.ll.playon.domain.guild.guild.dto.response.*;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.GuildTag;
import com.ll.playon.domain.guild.guild.enums.GuildMemberRole;
import com.ll.playon.domain.guild.guild.repository.GuildMemberRepositoryCustom;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guild.repository.WeeklyPopularGuildRepository;
import com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState;
import com.ll.playon.domain.guild.guildJoinRequest.repository.GuildJoinRequestRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.image.event.ImageDeleteEvent;
import com.ll.playon.domain.image.service.ImageService;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.service.TitleEvaluator;
import com.ll.playon.global.aws.s3.S3Service;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import com.ll.playon.global.validation.FileValidator;
import com.ll.playon.standard.page.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final WeeklyPopularGuildRepository weeklyPopularGuildRepository;
    private final S3Service s3Service;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final GuildJoinRequestRepository guildJoinRequestRepository;

    @Transactional
    public PostGuildResponse createGuild(PostGuildRequest request, Member owner) {
        // 파일 형식 확인
        FileValidator.validateFileType(request.fileType());

        // 이름 중복 확인
        if (guildRepository.existsByName(request.name())) {
            ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }

        // 게임 확인
        SteamGame game = null;
        if (request.appid() != null) {
            game = gameRepository.findByAppid(request.appid())
                    .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);
        }

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

        // 길드 생성 칭호
        titleEvaluator.check(ConditionType.GUILD_CREATE, owner);

        return PostGuildResponse.from(
                guild,
                genGuildPresignedUrl(guild.getId(), request.fileType())
        );
    }

    @Transactional
    public PutGuildResponse modifyGuild(Long guildId, PutGuildRequest request, Member actor) {
        // 파일 형식 확인
        FileValidator.validateFileType(request.newFileType());

        // 길드 권한 관련 확인
        Guild guild = getGuildOrThrow(guildId);
        GuildMember member = getGuildMemberOrThrow(guild, actor);
        validateIsManager(member);

        // 이름 중복 확인
        if (!guild.getName().equals(request.name()) &&
                guildRepository.existsByName(request.name())) {
            throw ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }

        // 게임 확인
        SteamGame game = null;
        if (request.appid() != null) {
            game = gameRepository.findByAppid(request.appid())
                    .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException);
        }

        // 이미지 수정인경우 -> 기존 이미지삭제, S3 삭제
        if (!request.newFileType().isBlank()) {
            imageService.deleteImagesByIdAndUrl(ImageType.GUILD, guild.getId(), guild.getGuildImg());
        }

        // 길드 수정
        guild.updateFromRequest(request, game);

        // 태그 수정
        guild.getGuildTags().clear();
        List<GuildTag> guildTags = convertTags(request.tags(), guild);
        guild.getGuildTags().addAll(guildTags);
        guildRepository.save(guild);

        return PutGuildResponse.from(
                guild,
                genGuildPresignedUrl(guild.getId(), request.newFileType())
        );
    }

    @Transactional
    public void saveImageUrl(long guildId, PostImageUrlRequest request) {
        // URL 확인
        if (ObjectUtils.isEmpty(request.url())) {
            throw ErrorCode.URL_NOT_FOUND.throwServiceException();
        }

        // 길드 저장
        guildRepository.findById(guildId).ifPresent(guild -> guild.changeGuildImg(request.url()));

        // 이미지 테이블 저장
        imageService.saveImage(ImageType.GUILD, guildId, request.url());
    }

    // PresignedUrl 발급
    private URL genGuildPresignedUrl(Long guildId, String fileType) {
        if(ObjectUtils.isNotEmpty(fileType)) {
            return s3Service.generatePresignedUrl(ImageType.GUILD, guildId, fileType);
        }

        return null;
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
        Guild guild = getGuildOrThrowWithTags(guildId);
        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor).orElse(null);

        if (!guild.isPublic() && guildMember == null) {
            throw ErrorCode.GUILD_NOT_FOUND.throwServiceException();
        }

        GuildMemberRole myRole;

        if (guildMember != null) {
            myRole = GuildMemberRole.valueOf(guildMember.getGuildRole().name());
        } else if (guildJoinRequestRepository.existsByGuildAndMemberAndApprovalState(guild, actor, ApprovalState.PENDING)) {
            myRole = GuildMemberRole.APPLICANT; // 가입 요청 상태
        } else {
            myRole = GuildMemberRole.GUEST; // 게스트
        }

        return GetGuildDetailResponse.from(guild, myRole);
    }

    @Transactional(readOnly = true)
    public GetGuildManageDetailResponse getGuildAdminDetail(Long guildId, Member actor) {
        Guild guild = getGuildOrThrowWithTags(guildId);

        GuildMember member = guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElseThrow(ErrorCode.GUILD_NO_PERMISSION::throwServiceException);

        if (member.isNotManagerOrLeader()) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        List<String> managerNames = guildMemberRepository.findManagerNicknamesByGuildId(guildId);

        return GetGuildManageDetailResponse.from(guild, member.getGuildRole().name(), managerNames);
    }

    @Transactional(readOnly = true)
    public List<getGuildMemberResponse> getGuildMembers(Long guildId, Member actor) {
        Guild guild = getGuildOrThrow(guildId);

        boolean isMember = guildMemberRepository.findByGuildAndMember(guild, actor).isPresent();

        // 비공개 + 멤버X 불가
        if (!guild.isPublic() && !isMember) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        List<GuildMember> members = guildMemberRepositoryCustom
                .findTopNByGuildOrderByRoleAndCreatedAt(guild, 9);

        return members.stream()
                .map(getGuildMemberResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageDto<GetGuildListResponse> searchGuilds(int page, int pageSize, String sort, GetGuildListRequest request) {
        Page<Guild> guilds = guildRepository.searchGuilds(request, PageRequest.of(page - 1, pageSize), sort);

        return new PageDto<>(guilds.map(GetGuildListResponse::from));
    }

    @Transactional(readOnly = true)
    public List<GetPopularGuildResponse> getPopularGuilds(LocalDate week) {
        List<Long> guildIds = weeklyPopularGuildRepository.findGuildIdsByWeek(week); // 이번주 인기 길드번호
        List<Guild> guilds = guildRepository.findAllById(guildIds); // 실제 길드

        Map<Long, Guild> guildMap = guilds.stream()
                .collect(Collectors.toMap(Guild::getId, g -> g)); // 순위 순서에 맞게

        return guildIds.stream()
                .map(guildMap::get)
                .map(GetPopularGuildResponse::from)
                .toList();
    }

    @Transactional
    public void deleteGuild(Long guildId, Member actor) {
        Guild guild = getGuildOrThrowWithTags(guildId);
        GuildMember member = getGuildMemberOrThrow(guild, actor);

        // 길드장만 삭제가능
        if (member.getGuildRole() != GuildRole.LEADER) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        // 태그 삭제
        guild.getGuildTags().clear();

        // 멤버 삭제
        guild.getMembers().clear();

        // 정보 마스킹
        guild.softDelete();

        guildRepository.save(guild); // 명시적으로 저장

        // 이미지 삭제
        applicationEventPublisher.publishEvent(new ImageDeleteEvent(guildId, ImageType.GUILD));
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

    private Guild getGuildOrThrowWithTags(Long id) {
        return guildRepository.findWithTagsById(id)
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
