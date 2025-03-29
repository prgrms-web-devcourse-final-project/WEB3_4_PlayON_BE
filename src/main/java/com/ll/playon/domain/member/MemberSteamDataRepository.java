package com.ll.playon.domain.member;

import com.ll.playon.domain.member.entity.MemberSteamData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSteamDataRepository extends JpaRepository<MemberSteamData, Long> {
}
