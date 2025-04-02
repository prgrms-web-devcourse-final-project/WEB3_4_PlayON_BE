package com.ll.playon.global.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ll.playon.global.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum TagType {
    PARTY_STYLE("파티 스타일"),
    GAME_SKILL("게임 실력"),
    GENDER("성별"),
    SOCIALIZING("친목");

    private final String value;
    private static final Map<String, TagType> CONVERT_MAP = Stream.of(values())
            .collect(Collectors.toMap(TagType::getKoreanValue, val -> val));

    @JsonValue
    public String getKoreanValue() {
        return value;
    }

    @JsonCreator
    public static TagType fromValue(String value) {
        TagType tagType = CONVERT_MAP.get(value);

        if (tagType == null) {
            ErrorCode.TAG_TYPE_CONVERT_FAILED.throwServiceException();
        }

        return tagType;
    }
}