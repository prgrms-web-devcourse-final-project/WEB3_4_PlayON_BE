package com.ll.playon.domain.board.service;

import com.ll.playon.domain.board.dto.request.PostBoardCommentRequest;
import com.ll.playon.domain.board.dto.request.PutBoardCommentRequest;
import com.ll.playon.domain.board.dto.response.GetBoardCommentResponse;
import com.ll.playon.domain.board.dto.response.PostBoardCommentResponse;
import com.ll.playon.domain.board.dto.response.PutBoardCommentResponse;
import com.ll.playon.domain.board.entity.Board;
import com.ll.playon.domain.board.entity.BoardComment;
import com.ll.playon.domain.board.repository.BoardCommentRepository;
import com.ll.playon.domain.board.repository.BoardRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.standard.page.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;

    @Transactional
    public PostBoardCommentResponse createComment(Long boardId, PostBoardCommentRequest request, Member actor) {
        // 게시글 존재 여부 체크
        Board board = findBoardOrElseThrow(boardId);

        // 댓글 저장
        BoardComment comment = BoardComment.builder()
                .board(board)
                .author(actor)
                .comment(request.comment())
                .build();

        board.addComment(comment);
        boardCommentRepository.save(comment);

        return PostBoardCommentResponse.builder()
                .commentId(comment.getId())
                .build();
    }

    @Transactional
    public PutBoardCommentResponse modifyComment(Long boardId, Long commentId, PutBoardCommentRequest request, Member actor) {
        // 게시글 존재 여부 체크
        findBoardOrElseThrow(boardId);

        // 댓글 존재 여부 체크
        BoardComment comment = findCommentOrElseThrow(commentId);

        // 댓글 작성자만 수정
        validateCommentAuthor(comment, actor);

        // 수정
        comment.updateComment(request.comment());

        return PutBoardCommentResponse.builder()
                .commentId(commentId)
                .build();
    }

    @Transactional
    public void deleteComment(Long boardId, Long commentId, Member actor) {
        // 게시글 존재 여부 체크
        Board board = findBoardOrElseThrow(boardId);

        // 댓글 존재 여부 체크
        BoardComment comment = findCommentOrElseThrow(commentId);

        // 댓글 작성자만 삭제
        validateCommentAuthor(comment, actor);

        // 댓글 삭제
        board.removeComment(comment);
        boardCommentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public PageDto<GetBoardCommentResponse> getComments(Long boardId, int page, int pageSize, Member actor) {
        // 게시글 존재 여부 체크
        findBoardOrElseThrow(boardId);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Long currentMemberId = actor != null ? actor.getId() : null;
        Page<GetBoardCommentResponse> comments = boardCommentRepository.findByBoardId(boardId, pageable, currentMemberId);

        return new PageDto<>(comments);
    }

    private Board findBoardOrElseThrow(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwServiceException);
    }

    private BoardComment findCommentOrElseThrow(Long commentId) {
        return boardCommentRepository.findById(commentId)
                .orElseThrow(ErrorCode.BOARD_COMMENT_NOT_FOUND::throwServiceException);
    }

    private void validateCommentAuthor(BoardComment comment, Member actor) {
        if (!comment.getAuthor().getId().equals(actor.getId())) {
            ErrorCode.NO_BOARD_COMMENT_PERMISSION.throwServiceException();
        }
    }
}
