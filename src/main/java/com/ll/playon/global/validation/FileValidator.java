package com.ll.playon.global.validation;

import com.ll.playon.global.exceptions.ErrorCode;

import java.util.List;

public class FileValidator {
    private static final List<String> ALLOWED_TYPES = List.of("png", "jpg", "jpeg", "webp");

    public static void validateFileType(String fileType) {
        // 파일 타입이 없음
        if (fileType == null || fileType.isBlank()) {
            throw ErrorCode.INVALID_FILE_TYPE.throwServiceException();
        }

        // 허용되지 않는 파일 타입
        if (!ALLOWED_TYPES.contains(fileType.toLowerCase())) {
            throw ErrorCode.INVALID_FILE_TYPE.throwServiceException();
        }
    }
}
