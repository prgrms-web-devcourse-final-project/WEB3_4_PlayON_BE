package com.ll.playon.global.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagValue {
    // 파티 스타일
    HARDCORE("빡겜"),
    CASUAL("즐겜"),
    SPEEDRUN("스피드런"),
    COMPLETIONIST("도전과제 콜렉터"),

    // 게임 실력
    ROTTEN_WATER("썩은물"),
    STAGNANT_WATER("고인물"),
    MUD_WATER("흙탕물"),
    CLEAN_WATER("청정수"),
    NEWBIE("뉴비"),

    // 성별
    MALE("남자만"),
    FEMALE("여자만"),

    // 친목
    SOCIAL_FRIENDLY("친목 환영"),
    GAME_ONLY("게임만 하고 싶어요"),
    NOC_CHAT("대화 안함");


    private final String koreanValue;

    @JsonValue
    public String getKoreanValue() {
        return koreanValue;
    }

    @JsonCreator
    public static TagValue fromValue(String value) {
        for (TagValue tagValue : values()) {
            if (tagValue.getKoreanValue().equals(value)) {
                return tagValue;
            }
        }

        throw ErrorCode.TAG_VALUE_CONVERT_FAILED.throwServiceException();
    }
}
