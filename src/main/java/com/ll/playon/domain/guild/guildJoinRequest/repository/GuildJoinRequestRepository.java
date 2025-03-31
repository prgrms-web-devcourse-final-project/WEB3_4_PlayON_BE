package com.ll.playon.domain.guild.guildJoinRequest.repository;

import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guildJoinRequest.entity.GuildJoinRequest;
import com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildJoinRequestRepository extends JpaRepository<GuildJoinRequest, Long> {
    boolean existsByGuildAndMemberAndApprovalState(Guild guild, Member member, ApprovalState approvalState);
}
