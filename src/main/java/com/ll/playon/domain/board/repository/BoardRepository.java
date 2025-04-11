package com.ll.playon.domain.board.repository;

import com.ll.playon.domain.board.dto.response.GetBoardListResponse;
import com.ll.playon.domain.board.entity.Board;
import com.ll.playon.domain.board.enums.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>{
    @Query("""
            SELECT b FROM Board b
            JOIN FETCH b.author
            WHERE b.id = :boardId
        """)
    Optional<Board> findByIdWithAuthor(@Param("boardId") Long boardId);

    @Query("""
            SELECT new com.ll.playon.domain.board.dto.response.GetBoardListResponse(
                b.id,
                COALESCE(m.nickname, m.username),
                img.imageUrl,
                t.name,
                b.title,
                SUBSTRING(b.content, 1, 100),
                b.hit,
                b.likeCount,
                (SELECT COUNT(c.id) FROM BoardComment c WHERE c.board.id = b.id),
                b.category,
                (SELECT i.imageUrl FROM Image i WHERE i.referenceId = b.id AND i.imageType = 'BOARD')
            )
            FROM Board b
            JOIN b.author m
            LEFT JOIN Image img ON img.referenceId = m.id AND img.imageType = 'MEMBER'
            LEFT JOIN MemberTitle mt ON mt.member = m AND mt.isRepresentative = true
            LEFT JOIN Title t ON mt.title = t
            WHERE (:category IS NULL OR b.category = :category)
            AND (:keyword IS NULL OR b.title LIKE %:keyword%)
            ORDER BY
                CASE WHEN :sort = 'LATEST' THEN b.createdAt END DESC,
                CASE WHEN :sort = 'POPULAR' THEN b.likeCount END DESC
        """)
    Page<GetBoardListResponse> findBoardList(BoardCategory category, String keyword, String sort, Pageable pageable);
}
