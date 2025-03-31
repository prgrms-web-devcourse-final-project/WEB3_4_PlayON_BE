package com.ll.playon.global.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Slf4j
public enum ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // common
    PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "페이지가 존재하지 않습니다"),

    // api
    EXTERNAL_API_UNEXPECTED_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "API 응답이 올바르지 않습니다"),
    EXTERNAL_API_COMMUNICATION_ERROR(HttpStatus.BAD_GATEWAY, "API 요청 중 오류가 발생했습니다."),

    // S3
    S3_PRESIGNED_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PresignedURL 생성에 실패하였습니다."),
    S3_OBJECT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 오브젝트 제거에 실패하였습니다."),
    S3_OBJECT_GET_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 오브젝트 호출에 실패하였습니다."),

    // Filtering
    PAGE_SIZE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "pageSize는 1에서 100 사이로 입력해주세요.");

    private final HttpStatus httpStatus;
    private final String message;

    public ServiceException throwServiceException() {
        throw new ServiceException(httpStatus, message);
    }

    public ServiceException throwServiceException(Throwable cause) {
        throw new ServiceException(httpStatus, message, cause);
    }
}