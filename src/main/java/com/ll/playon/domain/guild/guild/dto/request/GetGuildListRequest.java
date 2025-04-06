package com.ll.playon.domain.guild.guild.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetGuildListRequest {
        private String name;
        private List<Long> gameIds = new ArrayList<>();
        private Map<String, List<String>> tags = new HashMap<>();
}