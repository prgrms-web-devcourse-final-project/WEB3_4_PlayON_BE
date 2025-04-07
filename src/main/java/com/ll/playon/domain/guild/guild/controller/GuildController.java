package com.ll.playon.domain.guild.guild.controller;

import com.ll.playon.domain.guild.guild.dto.request.GetGuildListRequest;
import com.ll.playon.domain.guild.guild.dto.request.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.request.PutGuildRequest;
import com.ll.playon.domain.guild.guild.dto.response.*;
import com.ll.playon.domain.guild.guild.service.GuildService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.global.security.UserContext;
import com.ll.playon.global.validation.GlobalValidation;
import com.ll.playon.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guilds")
@RequiredArgsConstructor
public class GuildController {

    private final GuildService guildService;
    private final UserContext userContext;

    @PostMapping
    public RsData<PostGuildResponse> addGuild(@RequestBody @Valid PostGuildRequest request) {
        return RsData.success(HttpStatus.CREATED, guildService.createGuild(request, userContext.getActor()));
    }

    @PutMapping("/{guildId}")
    public RsData<PutGuildResponse> updateGuild(@PathVariable Long guildId,
                                                @RequestBody @Valid PutGuildRequest request) {
        return RsData.success(HttpStatus.OK, guildService.modifyGuild(guildId, request, userContext.getActor()));
    }

    @DeleteMapping("/{guildId}")
    public RsData<String> deleteGuild(@PathVariable Long guildId) {
        guildService.deleteGuild(guildId, userContext.getActor());
        return RsData.success(HttpStatus.OK, "ok");
    }

    @GetMapping("/{guildId}")
    public RsData<GetGuildDetailResponse> getGuildDetail(@PathVariable Long guildId) {
        return RsData.success(HttpStatus.OK, guildService.getGuildDetail(guildId, userContext.getActor()));
    }

//    @GetMapping("/{guildId}/members")
//    public RsData<PageDto<GuildMemberDto>> getGuildMembers(@PathVariable Long guildId, Pageable pageable) {
//        return RsData.success(HttpStatus.OK, guildService.getGuildMembers(guildId, userContext.getActor(), pageable));
//    }

    @GetMapping
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
    public RsData<List<GetPopularGuildResponse>> getPopularGuilds(@RequestParam(defaultValue = "10") int count) {
        return RsData.success(HttpStatus.OK, guildService.getPopularGuilds(count));
    }

    @GetMapping("/recommend")
    @Operation(summary = "특정 게임으로 길드 조회")
    public RsData<List<GetRecommendGuildResponse>> getRecommendGuild(@RequestParam(defaultValue = "5") int count, @RequestParam long appid) {
        return RsData.success(HttpStatus.OK, guildService.getRecommendedGuildsByGame(count, appid));
    }
}
