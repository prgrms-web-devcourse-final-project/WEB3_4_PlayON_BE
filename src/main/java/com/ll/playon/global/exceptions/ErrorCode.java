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

    // Filtering
    PAGE_SIZE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "pageSize는 1에서 100 사이로 입력해주세요."),

    // Guild
    DUPLICATE_GUILD_NAME(HttpStatus.CONFLICT, "이미 존재하는 길드 이름입니다."),
    GUILD_NOT_FOUND(HttpStatus.NOT_FOUND, "길드를 찾을 수 없습니다."),
    GUILD_NO_PERMISSION(HttpStatus.FORBIDDEN, "길드를 수정할 권한이 없습니다."),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게임을 찾을 수 없습니다."),
    GUILD_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "길드장 혼자일 떄만 길드를 삭제할 수 있습니다."),

    // GuildJoinRequest
    GUILD_ALREADY_REQUESTED(HttpStatus.CONFLICT, "이미 해당 길드에 가입 요청을 보냈습니다."),
    GUILD_JOIN_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "가입 요청을 찾을 수 없습니다."),
    GUILD_ID_MISMATCH(HttpStatus.BAD_REQUEST, "요청한 길드와 일치하지 않습니다."),
    GUILD_REQUEST_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 길드 요청입니다."),
    GUILD_APPROVAL_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "승인 권한이 없습니다."),

    // GuildMember
    GUILD_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "길드 멤버를 찾을 수 없습니다."),
    GUILD_LEADER_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "길드장은 탈퇴할 수 없습니다."),
    DELEGATE_MUST_BE_MANAGER(HttpStatus.BAD_REQUEST, "권한 위임은 운영진에게만 가능합니다."),
    ALREADY_MANAGER(HttpStatus.BAD_REQUEST, "이미 운영진 권한을 보유하고 있습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 멤버를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public ServiceException throwServiceException() {
        throw new ServiceException(httpStatus, message);
    }

    public ServiceException throwServiceException(Throwable cause) {
        throw new ServiceException(httpStatus, message, cause);
    }
}