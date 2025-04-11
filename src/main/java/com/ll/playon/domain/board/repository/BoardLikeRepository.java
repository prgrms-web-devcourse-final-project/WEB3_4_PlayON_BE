package com.ll.playon.domain.board.repository;

import com.ll.playon.domain.board.entity.Board;
import com.ll.playon.domain.board.entity.BoardLike;
import com.ll.playon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    boolean existsByBoardAndMember(Board board, Member actor);

    Optional<BoardLike> findByBoardAndMember(Board board, Member member);
}
