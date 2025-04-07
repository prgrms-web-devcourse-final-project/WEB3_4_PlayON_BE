package com.ll.playon.domain.guild.guildBoard.enums;

import org.springframework.data.domain.Sort;

public enum BoardSortType {
    LATEST("최신순", Sort.by(Sort.Direction.DESC, "createdAt")),
    POPULAR("인기순", Sort.by(Sort.Direction.DESC, "likeCount"));

    private final String label;
    private final Sort sort;

    BoardSortType(String label, Sort sort) {
        this.label = label;
        this.sort = sort;
    }

    public Sort getSort(){
        return sort;
    }
}
