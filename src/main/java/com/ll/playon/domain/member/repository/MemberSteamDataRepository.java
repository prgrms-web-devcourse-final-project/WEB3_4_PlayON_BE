package com.ll.playon.domain.member.repository;

import com.ll.playon.domain.member.entity.MemberSteamData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberSteamDataRepository extends JpaRepository<MemberSteamData, Long> {
    List<MemberSteamData> findAllByMemberId(Long id);

    @Query("SELECT m.appId FROM MemberSteamData m WHERE m.member.id = :memberId")
    List<Long> findAppIdsByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
