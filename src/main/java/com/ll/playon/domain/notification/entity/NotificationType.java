package com.ll.playon.domain.notification.entity;

public enum NotificationType {
    // 파티 관련 알림
    PARTY_SCHEDULE,       // 파티 일정
    PARTY_JOIN,           // 파티 참여
    PARTY_APPLICATION,    // 파티 참가 신청 여부
    PARTY_END,            // 파티 종료
    PARTY_LOG,            // 파티 로그
    PARTY_RESULT,         // 파티 결과
    PARTY_INVITE,         // 파티 초대

    // 길드 관련 알림
    GUILD_JOIN_REQUEST,   // 길드 가입 신청 여부
    GUILD_JOIN_APPROVED,  // 길드 가입 승인
    GUILD_LEAVE,          // 길드 탈퇴
    GUILD_MEMBER_JOIN,    // 길드 참여 알림 (운영진)
    GUILD_MEMBER_LEAVE    // 길드 탈퇴 알림 (운영진)
}
