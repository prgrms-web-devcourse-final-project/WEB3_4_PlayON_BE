package com.ll.playon.domain.guild.guildBoard.service;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCreateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.response.GuildBoardCreateResponse;
import com.ll.playon.domain.guild.guildBoard.dto.response.GuildBoardSummaryResponse;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardCommentRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildBoardService {
    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final GuildBoardRepository guildBoardRepository;
    private final GuildBoardCommentRepository guildBoardCommentRepository;

    public Page<GuildBoardSummaryResponse> getBoardList(Long guildId, BoardTag tag, Pageable pageable) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);

        Page<GuildBoard> boards = (tag != null)
                ? guildBoardRepository.findByGuildAndTag(guild, tag, pageable)
                : guildBoardRepository.findByGuild(guild, pageable);

        return boards.map(board -> {
            int commentCount = guildBoardCommentRepository.countByBoard(board);
            return GuildBoardSummaryResponse.from(board, commentCount);
        });
    }

    public GuildBoardCreateResponse createBoard(Long guildId, GuildBoardCreateRequest request, Member actor) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);

        GuildMember guildMember = guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElseThrow(ErrorCode.GUILD_MEMBER_NOT_FOUND::throwServiceException);

        // 공지글 작성 권한 체크
        if (request.tag() == BoardTag.NOTICE &&
                !guildMember.getGuildRole().isManagerOrLeader()) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        GuildBoard board = GuildBoard.builder()
                .guild(guild)
                .author(guildMember)
                .title(request.title())
                .content(request.content())
                .tag(request.tag())
                .imageUrl(request.imageUrl())
                .build();

        guildBoardRepository.save(board);

        return GuildBoardCreateResponse.from(board.getId());
    }

    public void deleteBoard(Long guildId, Long boardId, Member actor) {
        Guild guild=guildRepository.findById(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);

        GuildBoard board=guildBoardRepository.findById(boardId)
                .orElseThrow(ErrorCode.GUILD_BOARD_NOT_FOUND::throwServiceException);

        if(!board.getGuild().getId().equals(guild.getId())) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        if(!board.getAuthor().getMember().getId().equals(actor.getId())) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        guildBoardRepository.delete(board);
    }
}
