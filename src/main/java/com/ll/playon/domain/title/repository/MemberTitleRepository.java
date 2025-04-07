package com.ll.playon.domain.title.repository;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.entity.MemberTitle;
import com.ll.playon.domain.title.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberTitleRepository extends JpaRepository<MemberTitle, Long> {
    Optional<MemberTitle> findByMemberAndTitle(Member member, Title title);

    List<MemberTitle> findAllByMember(Member member);

    Optional<MemberTitle> findByMemberAndIsRepresentativeTrue(Member member);

    Optional<MemberTitle> findByIdAndMember(Long titleId, Member actor);
}
