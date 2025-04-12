package com.ll.playon.domain.board.repository;

import com.ll.playon.domain.board.dto.response.GetBoardCommentResponse;
import com.ll.playon.domain.board.entity.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    @Query("""
            SELECT new com.ll.playon.domain.board.dto.response.GetBoardCommentResponse(
                c.id,
                COALESCE(m.nickname, m.username),
                img.imageUrl,
                CASE WHEN :currentMemberId IS NOT NULL AND m.id = :currentMemberId THEN true ELSE false END,
                c.comment,
                c.createdAt
            )
            FROM BoardComment c
            JOIN c.author m
            LEFT JOIN Image img ON img.referenceId = m.id AND img.imageType = 'MEMBER'
            WHERE c.board.id = :boardId
        """)
    Page<GetBoardCommentResponse> findByBoardId(@Param("boardId") Long boardId, Pageable pageable, @Param("currentMemberId") Long currentMemberId);

}
