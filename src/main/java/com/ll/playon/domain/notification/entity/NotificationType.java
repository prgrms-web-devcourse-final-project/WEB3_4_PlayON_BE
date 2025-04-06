package com.ll.playon.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    // 파티 관련
    PARTY_SCHEDULE("파티 일정이 등록되었습니다.", "/parties/schedule", false),
    PARTY_JOIN("파티에 새로운 멤버가 참여했습니다.", "/parties/join", false),
    PARTY_APPLICATION("파티 참가 신청이 도착했습니다.", "/parties/applications", false),
    PARTY_END("파티가 종료되었습니다.", "/parties/history", false),
    PARTY_LOG("새로운 파티 로그가 등록되었습니다.", "/parties/logs", false),
    PARTY_RESULT("파티 결과가 등록되었습니다.", "/parties/results", false),
    PARTY_INVITE("파티에 초대되었습니다.", "/parties/invitations", false),

    // 길드 관련
    GUILD_JOIN_REQUEST("길드 가입 신청이 도착했습니다.", "/guilds/requests", true),
    GUILD_JOIN_APPROVED("길드 가입이 승인되었습니다.", "/guilds", false),
    GUILD_LEAVE("길드를 탈퇴하였습니다.", "/guilds", false),
    GUILD_MEMBER_JOIN("새로운 길드원이 가입했습니다.", "/guilds/members", false),
    GUILD_MEMBER_LEAVE("길드원이 탈퇴했습니다.", "/guilds/members", false);

    private final String defaultMessage;
    private final String defaultRedirectUrl;
    private final boolean actionable;

    NotificationType(String defaultMessage, String defaultRedirectUrl, boolean actionable) {
        this.defaultMessage = defaultMessage;
        this.defaultRedirectUrl = defaultRedirectUrl;
        this.actionable = actionable;
    }

    public String defaultMessage() {
        return defaultMessage;
    }

    public String defaultRedirectUrl() {
        return defaultRedirectUrl;
    }

}
