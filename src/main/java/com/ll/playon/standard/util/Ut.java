package com.ll.playon.standard.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.playon.global.app.AppConfig;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ut {
    public static class json {
        private static final ObjectMapper om = AppConfig.getObjectMapper();

        @SneakyThrows
        public static String toString(Object obj) {
            return om.writeValueAsString(obj);
        }

        @SneakyThrows
        public static Map<String, Object> toMap(String jsonStr) {
            return om.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
        }
    }
}
