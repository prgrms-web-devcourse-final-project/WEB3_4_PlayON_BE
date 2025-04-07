package com.ll.playon.domain.title.entity.enums;

public enum ConditionType {
    // 회원 가입 시
    REGISTERED,

    // 소유 게임 갯수 측정
    STEAM_GAME_COUNT,

    // 파티 참여 횟수 측정
    PARTY_JOIN_COUNT,

    // 파티 생성 횟수 측정
    PARTY_CREATE_COUNT,

    // 파티 누적 시간 측정
    PARTY_TIME_ACCUMULATED,

    // 파티로그 작성 횟수 측정
    PARTY_LOG_WRITE_COUNT,

    // MVP 투표한 횟수 측정
    MVP_VOTE_GIVEN,

    // MVP 추천 받은 횟수 측정
    MVP_VOTE_RECEIVED,

    // 길드 생성 시
    GUILD_CREATE,

    // 길드 게시물 작성 횟수 측정
    GUILD_POST_COUNT,

    // 길드 댓글 작성 횟수 측정
    GUILD_COMMENT_COUNT,

    // 자유게시판 게시물 작성 횟수 측정
    BOARD_POST_COUNT,

    // 자유게시판 댓글 작성 횟수 측정
    BOARD_COMMENT_COUNT
}
