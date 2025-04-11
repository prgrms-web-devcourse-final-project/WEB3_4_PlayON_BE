package com.ll.playon.domain.board.service;

import com.ll.playon.domain.board.entity.Board;
import com.ll.playon.domain.board.entity.BoardLike;
import com.ll.playon.domain.board.repository.BoardLikeRepository;
import com.ll.playon.domain.board.repository.BoardRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void like(Long boardId, Member actor) {
        // 게시글 존재 여부
        if (!boardRepository.existsById(boardId)) {
            throw ErrorCode.BOARD_NOT_FOUND.throwServiceException();
        }

        Board board = boardRepository.getReferenceById(boardId); // 프록시 객체 사용

        // 이미 좋아요 했는지 체크
        if (boardLikeRepository.existsByBoardAndMember(board, actor)) {
            throw ErrorCode.ALREADY_LIKED.throwServiceException();
        }

        boardLikeRepository.save(BoardLike.builder()
                        .board(board)
                        .member(actor)
                        .build());

        board.increaseLikeCount();
    }

    @Transactional
    public void cancelLike(Long boardId, Member actor) {
        if (!boardRepository.existsById(boardId)) {
            throw ErrorCode.BOARD_NOT_FOUND.throwServiceException();
        }

        Board board = boardRepository.getReferenceById(boardId);

        BoardLike like = boardLikeRepository.findByBoardAndMember(board, actor)
                .orElseThrow(ErrorCode.BOARD_LIKE_NOT_FOUND::throwServiceException);

        boardLikeRepository.delete(like);
        board.decreaseLikeCount();
    }
}
