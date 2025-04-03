package com.ll.playon.domain.member.repository;

import com.ll.playon.domain.member.entity.MemberSteamData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberSteamDataRepository extends JpaRepository<MemberSteamData, Long> {
    List<MemberSteamData> findAllByMemberId(Long id);
}
