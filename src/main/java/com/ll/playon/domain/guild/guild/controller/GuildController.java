package com.ll.playon.domain.guild.guild.controller;

import com.ll.playon.domain.guild.guild.dto.request.GetGuildListRequest;
import com.ll.playon.domain.guild.guild.dto.request.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.request.PutGuildRequest;
import com.ll.playon.domain.guild.guild.dto.response.*;
import com.ll.playon.domain.guild.guild.service.GuildService;
import com.ll.playon.domain.image.type.ImageType;
import com.ll.playon.global.aws.s3.S3Service;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.validation.FileValidator;
import com.ll.playon.global.validation.GlobalValidation;
import com.ll.playon.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.time.Instant;
import java.util.List;

@Tag(name = "Guild", description = "길드 기능")
@RestController
@RequestMapping("/api/guilds")
@RequiredArgsConstructor
public class GuildController {

    private final GuildService guildService;
    private final UserContext userContext;
    private final S3Service s3Service;

    @GetMapping("/presigned-url")
    @Operation(summary = "길드 대표 이미지 업로드용 Presigned URL 발급")
    public RsData<URL> getPresignedUrl(@RequestParam String fileType) {
        FileValidator.validateFileType(fileType); // 파일 타입 체크

        // presigned URL은 아직 길드가 생성되기 전이므로 임시 ID 사용
        return RsData.success(HttpStatus.OK, s3Service.generatePresignedUrl(ImageType.GUILD, Instant.now().getEpochSecond(), fileType));
    }

    @PostMapping
    @Operation(summary = "길드 생성")
    public RsData<PostGuildResponse> addGuild(@RequestBody @Valid PostGuildRequest request) {
        return RsData.success(HttpStatus.CREATED, guildService.createGuild(request, userContext.getActor()));
    }

    @PutMapping("/{guildId}")
    @Operation(summary = "길드 수정")
    public RsData<PutGuildResponse> updateGuild(@PathVariable Long guildId,
                                                @RequestBody @Valid PutGuildRequest request) {
        return RsData.success(HttpStatus.OK, guildService.modifyGuild(guildId, request, userContext.getActor()));
    }

    @DeleteMapping("/{guildId}")
    @Operation(summary = "길드 삭제(길드장만 가능)")
    public RsData<String> deleteGuild(@PathVariable Long guildId) {
        guildService.deleteGuild(guildId, userContext.getActor());
        return RsData.success(HttpStatus.OK, "ok");
    }

    @GetMapping("/{guildId}")
    @Operation(summary = "길드 상세 조회")
    public RsData<GetGuildDetailResponse> getGuildDetail(@PathVariable Long guildId) {
        return RsData.success(HttpStatus.OK, guildService.getGuildDetail(guildId, userContext.getActor()));
    }

//    @GetMapping("/{guildId}/members")
//    @Operation(summary = "길드 멤버 조회")
//    public RsData<PageDto<GuildMemberDto>> getGuildMembers(@PathVariable Long guildId, Pageable pageable) {
//        return RsData.success(HttpStatus.OK, guildService.getGuildMembers(guildId, userContext.getActor(), pageable));
//    }

    @GetMapping
    @Operation(summary = "길드 상세 검색")
    public RsData<PageDto<GetGuildListResponse>> searchGuilds(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "latest") String sort,
            @ModelAttribute @Valid GetGuildListRequest request
    ) {
        GlobalValidation.checkPageSize(pageSize);
        
        return RsData.success(HttpStatus.OK, guildService.searchGuilds(page, pageSize, sort, request));
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 길드 조회")
    public RsData<List<GetPopularGuildResponse>> getPopularGuilds(@RequestParam(defaultValue = "10") int count) {
        return RsData.success(HttpStatus.OK, guildService.getPopularGuilds(count));
    }

    @GetMapping("/recommend")
    @Operation(summary = "특정 게임으로 길드 조회")
    public RsData<List<GetRecommendGuildResponse>> getRecommendGuild(@RequestParam(defaultValue = "5") int count, @RequestParam long appid) {
        return RsData.success(HttpStatus.OK, guildService.getRecommendedGuildsByGame(count, appid));
    }
}
