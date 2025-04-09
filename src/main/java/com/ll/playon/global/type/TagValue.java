package com.ll.playon.global.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TagValue {
    // 파티 스타일
    BEGINNER("맛보기"),
    CASUAL("캐주얼"),
    NORMAL("노멀"),
    HARDCORE("하드"),
    EXTREME("익스트림"),
    COMPLETIONIST("도전과제"),
    SPEEDRUN("스피드러너"),

    // 게임 실력
    MASTER("마스터"),
    HACKER("해커"),
    PRO("프로"),
    NEWBIE("뉴비"),

    // 성별
    MALE("남자만"),
    FEMALE("여자만"),

    // 친목
    SOCIAL_FRIENDLY("친목 환영"),
    GAME_ONLY("게임 전용"),
    NO_CHAT("대화 없음");

    private final String koreanValue;

    @JsonValue
    public String getKoreanValue() {
        return koreanValue;
    }

    @JsonCreator
    public static TagValue fromValue(String value) {
        return Arrays.stream(values())
                .filter(tagValue -> tagValue.getKoreanValue().equals(value))
                .findFirst()
                .orElseThrow(ErrorCode.TAG_VALUE_CONVERT_FAILED::throwServiceException);
    }
}
