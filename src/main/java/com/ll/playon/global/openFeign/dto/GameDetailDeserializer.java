package com.ll.playon.global.openFeign.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class GameDetailDeserializer extends JsonDeserializer<GameDetail> {
    @Override
    public GameDetail deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // data 필드가 배열([])이면 null 반환
        if (node.isArray()) {
            return null;
        }

        // 정상적인 객체이면 Jackson이 자동 매핑
        return p.getCodec().treeToValue(node, GameDetail.class);
    }
}