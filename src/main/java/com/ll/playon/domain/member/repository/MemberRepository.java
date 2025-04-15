package com.ll.playon.domain.member.repository;

import com.ll.playon.domain.board.dto.MemberProfileDto;
import com.ll.playon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByApiKey(String apiKey);
    Optional<Member> findBySteamId(Long steamId);

    Optional<Member> findByUsername(String username);

    List<Member> findByNickname(String nickname);

    @Query("""
            SELECT new com.ll.playon.domain.board.dto.MemberProfileDto(
                m.id,
                COALESCE(m.nickname, m.username),
                m.profileImg,
                t.name
            )
            FROM Member m
            LEFT JOIN MemberTitle mt ON mt.member = m AND mt.isRepresentative = true
            LEFT JOIN Title t ON mt.title = t
            WHERE m.id = :memberId
        """)
    Optional<MemberProfileDto> getProfile(@Param("memberId") Long memberId);
}
