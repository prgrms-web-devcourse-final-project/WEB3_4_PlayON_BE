package com.ll.playon.domain.guild.guild.controller;

import com.ll.playon.domain.guild.guild.dto.*;
import com.ll.playon.domain.guild.guild.enums.GuildDetailDto;
import com.ll.playon.domain.guild.guild.service.GuildService;
import com.ll.playon.global.response.RsData;
import com.ll.playon.standard.page.dto.PageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guilds")
@RequiredArgsConstructor
public class GuildController {

    private final GuildService guildService;

    @PostMapping
    public RsData<PostGuildResponse> addGuild(@RequestBody @Valid PostGuildRequest request) {
        PostGuildResponse response = guildService.createGuild(request);
        return RsData.success(HttpStatus.CREATED, response);
    }

    @PutMapping("/{guildId}")
    public RsData<PutGuildResponse> updateGuild(@PathVariable Long guildId,
                                                @RequestBody @Valid PutGuildRequest request) {
        PutGuildResponse response = guildService.modifyGuild(guildId, request);
        return RsData.success(HttpStatus.OK, response);
    }

    @DeleteMapping("/{guildId}")
    public RsData<String> deleteGuild(@PathVariable Long guildId) {
        guildService.deleteGuild(guildId);
        return RsData.success(HttpStatus.OK, "ok");
    }

    @GetMapping("/{guildId}")
    public RsData<GuildDetailDto> getGuildDetail(@PathVariable Long guildId) {
        GuildDetailDto detail = guildService.getGuildDetail(guildId);
        return RsData.success(HttpStatus.OK, detail);
    }

//    @GetMapping("/{guildId}/members")
//    public RsData<PageDto<GuildMemberDto>> getGuildMembers(@PathVariable Long guildId, Pageable pageable) {
//        PageDto<GuildMemberDto> response = guildService.getGuildMembers(guildId, pageable);
//        return RsData.success(HttpStatus.OK, response);
//    }

    @GetMapping("/search")
    public RsData<PageDto<GetGuildListResponse>> searchGuilds(@ModelAttribute @Valid GetGuildListRequest request) {
        PageDto<GetGuildListResponse> result = guildService.searchGuilds(request);
        return RsData.success(HttpStatus.OK, result);
    }
}
