package com.ll.playon.domain.party.party.util;

import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class PartySortUtils {
    private static final Map<String, Sort> SORT_MAP = Map.of(
            "latest", Sort.by(Direction.DESC, "createdAt"),
            "popular", Sort.by(Direction.DESC, "hit")
    );

    public static Sort getSort(String orderBy) {
        return SORT_MAP.getOrDefault(orderBy, SORT_MAP.get("latest"));
    }
}
