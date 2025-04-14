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
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.guild.util.GuildPermissionValidator;
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

    /**
     * 길드 생성
     */
    @Transactional
    public PostGuildResponse createGuild(PostGuildRequest request, Member owner) {
        FileValidator.validateFileType(request.fileType());
        checkDuplicateName(request.name());

        // 길드 저장
        Guild guild = guildRepository.save(Guild.createFrom(request, owner, getGame(request.appid())));

        // 태그 설정
        guild.setGuildTags(convertTags(request.tags(), guild));

        // 길드장
        guildMemberRepository.save(GuildMember.createLeader(owner, guild));

        // 길드 생성 칭호
        titleEvaluator.check(ConditionType.GUILD_CREATE, owner);

        return PostGuildResponse.from(
                guild,
                genGuildPresignedUrl(guild.getId(), request.fileType())
        );
    }

    /**
     * 길드 수정
     */
    @Transactional
    public PutGuildResponse modifyGuild(Long guildId, PutGuildRequest request, Member actor) {
        FileValidator.validateFileType(request.newFileType());

        // 길드 권한 관련 확인
        Guild guild = getGuildOrThrow(guildId);
        GuildMember guildMember = getGuildMemberOrThrow(guild, actor);
        GuildPermissionValidator.checkManagerOrLeader(guildMember);

        // 이름 중복 확인
        if (!guild.getName().equals(request.name())) {
            checkDuplicateName(request.name());
        }

        // 게임 확인
        SteamGame game = getGame(request.appid());

        // 이미지 수정인경우 -> 기존 이미지삭제, S3 삭제
        if (!request.newFileType().isBlank()) {
            imageService.deleteImagesByIdAndUrl(ImageType.GUILD, guild.getId(), guild.getGuildImg());
        }

        // 길드 수정
        guild.updateFromRequest(request, game);

        // 태그 수정
        guild.getGuildTags().clear();
        guild.getGuildTags().addAll(convertTags(request.tags(), guild));
        guildRepository.save(guild);

        return PutGuildResponse.from(
                guild,
                genGuildPresignedUrl(guild.getId(), request.newFileType())
        );
    }

    /**
     * 이미지 URL 저장
     */
    @Transactional
    public void saveImageUrl(long guildId, PostImageUrlRequest request) {
        // URL 확인
        if (ObjectUtils.isEmpty(request.url())) {
            throw ErrorCode.URL_NOT_FOUND.throwServiceException();
        }

        // 길드 저장
        guildRepository.findById(guildId)
                .ifPresent(guild -> guild.changeGuildImg(request.url()));

        // 이미지 테이블 저장
        imageService.saveImage(ImageType.GUILD, guildId, request.url());
    }

    /**
     * 길드 상세정보 조회
     * 비공개 길드는 멤버만 가능
     * 공개 길드는 누구나 가능
     */
    @Transactional(readOnly = true)
    public GetGuildDetailResponse getGuildDetail(Long guildId, Member actor) {
        Guild guild = getGuildOrThrowWithTags(guildId);
        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElse(null);

        // 공개여부 확인
        GuildPermissionValidator.checkPublicOrMember(guild, guildMember);

        // 역할 확인
        GuildMemberRole myRole = getMyRole(guild, guildMember, actor);

        return GetGuildDetailResponse.from(guild, myRole);
    }

    /**
     * 길드 상세조회 관리자페이지용
     */
    @Transactional(readOnly = true)
    public GetGuildManageDetailResponse getGuildAdminDetail(Long guildId, Member actor) {
        Guild guild = getGuildOrThrowWithTags(guildId);
        GuildMember guildMember = getGuildMemberOrThrow(guild, actor);

        // 운영진 권한 확인
        GuildPermissionValidator.checkManagerOrLeader(guildMember);

        List<String> managerNames = guildMemberRepository.findManagerNicknamesByGuildId(guildId);

        return GetGuildManageDetailResponse.from(
                guild,
                guildMember.getGuildRole().name(),
                managerNames
        );
    }

    /**
     * 길드 멤버 조회
     */
    @Transactional(readOnly = true)
    public List<getGuildMemberResponse> getGuildMembers(Long guildId, Member actor) {
        Guild guild = getGuildOrThrow(guildId);
        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElse(null);

        // 비공개 + 멤버X 불가
        GuildPermissionValidator.checkPublicOrMember(guild, guildMember);

        List<GuildMember> members = guildMemberRepositoryCustom
                .findTopNByGuildOrderByRoleAndCreatedAt(guild, 9);

        return members.stream()
                .map(getGuildMemberResponse::from)
                .toList();
    }

    /**
     * 길드 검색
     */
    @Transactional(readOnly = true)
    public PageDto<GetGuildListResponse> searchGuilds(int page, int pageSize, String sort, GetGuildListRequest request) {
        Page<Guild> guilds = guildRepository.searchGuilds(
                request,
                PageRequest.of(page - 1, pageSize),
                sort
        );
        return new PageDto<>(guilds.map(GetGuildListResponse::from));
    }

    /**
     * 인기 길드
     * 일주일간 길드 게시판 글 작성 많은 순
     */
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

    /**
     * 길드 삭제
     */
    @Transactional
    public void deleteGuild(Long guildId, Member actor) {
        Guild guild = getGuildOrThrowWithTags(guildId);
        GuildMember member = getGuildMemberOrThrow(guild, actor);

        // 길드장만 삭제가능
        GuildPermissionValidator.checkLeader(member);

        // 태그,멤버 삭제
        guild.getGuildTags().clear();
        guild.getMembers().clear();

        // 정보 마스킹
        guild.softDelete();
        guildRepository.save(guild); // 명시적으로 저장

        // 이미지 삭제
        applicationEventPublisher.publishEvent(new ImageDeleteEvent(guildId, ImageType.GUILD));
    }

    /**
     * 특정 게임을 선택한 길드
     */
    @Transactional(readOnly = true)
    public List<GetRecommendGuildResponse> getRecommendedGuildsByGame(int count, long appid) {
        return guildRepository.findTopNByGameAppid(appid, PageRequest.of(0, count))
                .stream()
                .map(GetRecommendGuildResponse::from)
                .toList();
    }




    // 게임 확인
    private SteamGame getGame(Long appid) {
        return appid != null
                ? gameRepository.findByAppid(appid)
                        .orElseThrow(ErrorCode.GAME_NOT_FOUND::throwServiceException)
                : null;
    }

    // Presigned URL 발급
    private URL genGuildPresignedUrl(Long guildId, String fileType) {
        return ObjectUtils.isNotEmpty(fileType)
                ? s3Service.generatePresignedUrl(ImageType.GUILD, guildId, fileType)
                : null;
    }

    // 길드 조회에서 권한
    private GuildMemberRole getMyRole(Guild guild, GuildMember guildMember, Member actor) {
        if (guildMember != null) {
            return GuildMemberRole.valueOf(guildMember.getGuildRole().name());
        }

        // 가입 신청중 -> APPLICANT
        boolean isApplicant = guildJoinRequestRepository.existsByGuildAndMemberAndApprovalState(
                guild, actor, ApprovalState.PENDING
        );

        return isApplicant ? GuildMemberRole.APPLICANT : GuildMemberRole.GUEST;
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

    // 삭제되지 않은 길드
    private Guild getGuildOrThrow(Long guildId) {
        return guildRepository.findByIdAndIsDeletedFalse(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);
    }

    // 길드 + 태그
    private Guild getGuildOrThrowWithTags(Long id) {
        return guildRepository.findWithTagsById(id)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);
    }

    // 길드 멤버인지
    private GuildMember getGuildMemberOrThrow(Guild guild, Member member) {
        return guildMemberRepository.findByGuildAndMember(guild, member)
                .orElseThrow(ErrorCode.GUILD_NO_PERMISSION::throwServiceException);
    }

    // 길드 이름 중복 확인
    private void checkDuplicateName(String name) {
        if (guildRepository.existsByName(name)) {
            throw ErrorCode.DUPLICATE_GUILD_NAME.throwServiceException();
        }
    }
}
