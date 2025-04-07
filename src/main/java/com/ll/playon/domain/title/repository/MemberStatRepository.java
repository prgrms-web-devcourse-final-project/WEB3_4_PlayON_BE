package com.ll.playon.domain.title.repository;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.entity.MemberStat;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberStatRepository extends JpaRepository<MemberStat, Long> {
    Optional<MemberStat> findByMemberAndConditionType(Member member, ConditionType conditionType);
}
