package com.ll.playon.standard.page.dto;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import java.util.List;

public record PageDto<T>(
        @NonNull int currentPageNumber,
        @NonNull int pageSize,
        @NonNull long totalPages,
        @NonNull long totalItems,
        @NonNull List<T> items
) {
    public PageDto(Page<T> page) {
        this(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getContent()
        );
    }
}
