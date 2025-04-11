package com.ll.playon.domain.party.party.util;

import static org.springframework.data.domain.Sort.Order;
import static org.springframework.data.domain.Sort.by;

import org.springframework.data.domain.Sort;

public class PartySortUtils {
    public static Sort getSort(String orderBy) {
        return switch (orderBy) {
            case "popular" -> by(Order.desc("hit"), Order.asc("partyAt"));
            case "partyAt" -> by(Order.asc("partyAt"), Order.desc("createdAt"));
            case "personal" -> by(Order.desc("total"), Order.asc("partyAt"));
            default -> by(Order.desc("createdAt"), Order.asc("partyAt"));
        };
    }
}
