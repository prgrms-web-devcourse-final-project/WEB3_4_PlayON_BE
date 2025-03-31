package com.ll.playon.domain.guild.guild.service;

import com.ll.playon.domain.guild.guild.dto.GuildDto;
import com.ll.playon.domain.guild.guild.dto.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.PostGuildResponse;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;
    private final UserContext userContext;
    private final GuildMemberRepository guildMemberRepository;

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
}
