package com.ll.playon.domain.guild.guild.controller;

import com.ll.playon.domain.guild.guild.dto.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.PostGuildResponse;
import com.ll.playon.domain.guild.guild.dto.PutGuildRequest;
import com.ll.playon.domain.guild.guild.dto.PutGuildResponse;
import com.ll.playon.domain.guild.guild.service.GuildService;
import com.ll.playon.global.response.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guilds")
@RequiredArgsConstructor
public class GuildController {

    private final GuildService guildService;

    @PostMapping
    public RsData<PostGuildResponse> addGuild(@RequestBody PostGuildRequest request) {
        PostGuildResponse response = guildService.createGuild(request);
        return RsData.success(HttpStatus.CREATED, response);
    }

    @PutMapping("/{guildId}")
    public RsData<PutGuildResponse> updateGuild(@PathVariable Long guildId,
                                                @RequestBody PutGuildRequest request) {
        PutGuildResponse response = guildService.updateGuild(guildId, request);
        return RsData.success(HttpStatus.OK, response);
    }
}
