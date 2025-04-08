package com.ll.playon.domain.guild.guildBoard.service;

import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildBoard.dto.GuildBoardCommentDto;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCommentCreateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCommentUpdateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardCreateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.request.GuildBoardUpdateRequest;
import com.ll.playon.domain.guild.guildBoard.dto.response.*;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardComment;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardLike;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardCommentRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardLikeRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuildBoardService {

    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final GuildBoardRepository guildBoardRepository;
    private final GuildBoardCommentRepository guildBoardCommentRepository;
    private final GuildBoardLikeRepository guildBoardLikeRepository;

    public Page<GuildBoardSummaryResponse> getBoardList(Long guildId, BoardTag tag, String keyword, Pageable pageable) {
        Guild guild = getGuild(guildId);

        Page<GuildBoard> boards = switchBoardSearch(tag, keyword, pageable, guild);

        return boards.map(board -> {
            int commentCount = guildBoardCommentRepository.countByBoard(board);
            return GuildBoardSummaryResponse.from(board, commentCount);
        });
    }

    public GuildBoardCreateResponse createBoard(Long guildId, GuildBoardCreateRequest request, Member actor) {
        Guild guild = getGuild(guildId);
        GuildMember guildMember = getGuildMember(guild, actor);

        validateNoticePermission(request.tag(), guildMember);

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

    public void updateBoard(Long guildId, Long boardId, GuildBoardUpdateRequest request, Member actor) {
        GuildBoard board = getBoardInGuild(guildId, boardId);
        validateAuthor(board, actor);
        validateNoticePermission(request.tag(), board.getAuthor());

        board.update(request.title(), request.content(), request.tag(), request.imageUrl());
    }

    public void deleteBoard(Long guildId, Long boardId, Member actor) {
        GuildBoard board = getBoardInGuild(guildId, boardId);
        validateAuthor(board, actor);

        guildBoardRepository.delete(board);
    }

    public GuildBoardDetailResponse getBoardDetail(Long guildId, Long boardId, Member actor) {
        Guild guild = getGuild(guildId);
        GuildMember guildMember = getGuildMember(guild, actor);
        GuildBoard board = getBoardInGuild(guildId, boardId);

        board.increaseHit();

        List<GuildBoardCommentDto> comments = guildBoardCommentRepository
                .findByBoardOrderByCreatedAtAsc(board)
                .stream()
                .map(GuildBoardCommentDto::from)
                .toList();

        return GuildBoardDetailResponse.from(board, comments);
    }

    @Transactional
    public GuildBoardLikeToggleResponse toggleLike(Long guildId, Long boardId, Member actor) {
        Guild guild = getGuild(guildId);
        GuildMember guildMember = getGuildMember(guild, actor);
        GuildBoard board = getBoardInGuild(guildId, boardId);

        Optional<GuildBoardLike> existingLike = guildBoardLikeRepository.findByGuildMemberAndBoard(guildMember, board);

        boolean liked;
        if (existingLike.isPresent()) {
            guildBoardLikeRepository.delete(existingLike.get());
            board.decreaseLike();
            liked = false;
        } else {
            GuildBoardLike like = GuildBoardLike.builder()
                    .guildMember(guildMember)
                    .build();

            board.addLike(like);

            guildBoardLikeRepository.save(like);
            board.increaseLike();
            liked = true;
        }

        return GuildBoardLikeToggleResponse.of(liked, board.getLikeCount());
    }


    @Transactional
    public GuildBoardCommentCreateResponse createComment(Long guildId, Long boardId, GuildBoardCommentCreateRequest request, Member actor) {
        Guild guild = getGuild(guildId);
        GuildBoard board = getBoardInGuild(guildId, boardId);
        GuildMember guildMember = getGuildMember(guild, actor);

        GuildBoardComment comment = GuildBoardComment.builder()
                .author(guildMember)
                .comment(request.comment())
                .build();

        board.addComment(comment);

        guildBoardCommentRepository.save(comment);

        return GuildBoardCommentCreateResponse.from(comment);
    }


    @Transactional
    public void updateComment(Long guildId, Long boardId, Long commentId, GuildBoardCommentUpdateRequest request, Member actor) {
        GuildBoard board = getBoardInGuild(guildId, boardId);
        GuildBoardComment comment = getComment(commentId);

        validateCommentBelongsToBoard(comment, board);
        validateAuthor(comment, actor);

        comment.update(request.comment());
    }

    @Transactional
    public void deleteComment(Long guildId, Long boardId, Long commentId, Member actor) {
        GuildBoard board = getBoardInGuild(guildId, boardId);
        GuildBoardComment comment = getComment(commentId);

        validateCommentBelongsToBoard(comment, board);
        validateAuthor(comment, actor);

        guildBoardCommentRepository.delete(comment);
    }


    private Guild getGuild(Long guildId) {
        return guildRepository.findById(guildId)
                .orElseThrow(ErrorCode.GUILD_NOT_FOUND::throwServiceException);
    }

    private GuildMember getGuildMember(Guild guild, Member actor) {
        return guildMemberRepository.findByGuildAndMember(guild, actor)
                .orElseThrow(ErrorCode.GUILD_MEMBER_NOT_FOUND::throwServiceException);
    }

    private GuildBoard getBoardInGuild(Long guildId, Long boardId) {
        GuildBoard board = guildBoardRepository.findById(boardId)
                .orElseThrow(ErrorCode.GUILD_BOARD_NOT_FOUND::throwServiceException);

        if (!board.getGuild().getId().equals(guildId)) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }

        return board;
    }

    private GuildBoardComment getComment(Long commentId) {
        return guildBoardCommentRepository.findById(commentId)
                .orElseThrow(ErrorCode.GUILD_BOARD_COMMENT_NOT_FOUND::throwServiceException);
    }

    private void validateAuthor(GuildBoard board, Member actor) {
        if (!board.getAuthor().getMember().getId().equals(actor.getId())) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    private void validateAuthor(GuildBoardComment comment, Member actor) {
        if (!comment.getAuthor().getMember().getId().equals(actor.getId())) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    private void validateCommentBelongsToBoard(GuildBoardComment comment, GuildBoard board) {
        if (!comment.getBoard().getId().equals(board.getId())) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    private void validateNoticePermission(BoardTag tag, GuildMember guildMember) {
        if (tag == BoardTag.NOTICE && !guildMember.getGuildRole().isManagerOrLeader()) {
            throw ErrorCode.GUILD_NO_PERMISSION.throwServiceException();
        }
    }

    private Page<GuildBoard> switchBoardSearch(BoardTag tag, String keyword, Pageable pageable, Guild guild) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (tag != null && hasKeyword) {
            return guildBoardRepository.findByGuildAndTagAndTitleContaining(guild, tag, keyword, pageable);
        } else if (tag != null) {
            return guildBoardRepository.findByGuildAndTag(guild, tag, pageable);
        } else if (hasKeyword) {
            return guildBoardRepository.findByGuildAndTitleContaining(guild, keyword, pageable);
        } else {
            return guildBoardRepository.findByGuild(guild, pageable);
        }
    }
}

