package com.ll.playon.domain.member.repository;

import com.ll.playon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByApiKey(String apiKey);
    Optional<Member> findBySteamId(Long steamId);

    Optional<Member> findByUsername(String username);

    List<Member> findByNickname(String nickname);
}
