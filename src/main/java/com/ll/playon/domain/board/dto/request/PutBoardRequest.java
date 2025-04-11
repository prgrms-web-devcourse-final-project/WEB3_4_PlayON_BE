package com.ll.playon.domain.board.dto.request;

import com.ll.playon.domain.board.enums.BoardCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.net.URL;

public record PutBoardRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 50, message = "제목은 최대 50자까지 가능합니다.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        @NotNull(message = "카테고리를 선택해주세요.")
        BoardCategory category,

        String newFileType,

        URL deleteUrl
) {
}
