package com.ll.playon.domain.title.repository;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.dto.RepresentativeTitleDto;
import com.ll.playon.domain.title.entity.MemberTitle;
import com.ll.playon.domain.title.entity.Title;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

public interface MemberTitleRepository extends JpaRepository<MemberTitle, Long> {
    Optional<MemberTitle> findByMemberAndTitle(Member member, Title title);

    List<MemberTitle> findAllByMember(Member member);

    Optional<MemberTitle> findByMemberAndIsRepresentativeTrue(Member member);

    Optional<MemberTitle> findByTitleIdAndMember(Long titleId, Member actor);

    @Query("""
            SELECT NEW com.ll.playon.domain.title.dto.RepresentativeTitleDto(mt.member.id, t.name)
            FROM MemberTitle mt
            JOIN mt.title t
            WHERE mt.isRepresentative = true
            AND mt.member.id IN :memberIds
            """)
    List<RepresentativeTitleDto> findRepresentativeTitleByMemberIds(@PathVariable("memberIds") List<Long> memberIds);
}
