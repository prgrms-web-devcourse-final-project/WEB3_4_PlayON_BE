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
    EXTERNAL_API_UNEXPECTED_REQUEST(HttpStatus.BAD_REQUEST, "API 요청이 올바르지 않습니다"),
    EXTERNAL_API_COMMUNICATION_ERROR(HttpStatus.BAD_GATEWAY, "API 요청 중 오류가 발생했습니다."),

    // S3
    S3_PRESIGNED_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PresignedURL 생성에 실패하였습니다."),
    S3_OBJECT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 오브젝트 제거에 실패하였습니다."),
    S3_OBJECT_GET_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 오브젝트 호출에 실패하였습니다."),

    // 이미지
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),

    // Filtering
    PAGE_SIZE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "pageSize는 1에서 100 사이로 입력해주세요."),

    // Auth
    AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED, "인증이 실패하였습니다."),
    USER_NOT_REGISTERED(HttpStatus.NOT_FOUND, "가입되지 않은 사용자입니다."),
    USER_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 가입된 사용자입니다."),
    PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // EventListener
    EVENT_LISTENER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이벤트 리스너에서 오류가 발생했습니다."),

    // Guild
    DUPLICATE_GUILD_NAME(HttpStatus.CONFLICT, "이미 존재하는 길드 이름입니다."),
    GUILD_NOT_FOUND(HttpStatus.NOT_FOUND, "길드를 찾을 수 없습니다."),
    GUILD_NO_PERMISSION(HttpStatus.FORBIDDEN, "길드 권한이 없습니다."),
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
    NOT_MANAGER(HttpStatus.BAD_REQUEST, "운영진 권한이 없는 유저입니다."),
    CANNOT_EXPEL_LEADER(HttpStatus.BAD_REQUEST, "길드장은 강제 퇴출할 수 없습니다."),
    ALREADY_GUILD_MEMBER(HttpStatus.BAD_REQUEST, "이미 해당 길드에 가입한 멤버입니다."),
    CANNOT_DELEGATE_TO_SINGLE_MANAGER(HttpStatus.BAD_REQUEST, "운영진이 2명 이상일 때만 권한 위임이 가능합니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 멤버를 찾을 수 없습니다."),

    // Party
    IS_ALREADY_REQUEST_PARTY(HttpStatus.FORBIDDEN, "파티 신청이 진행 중입니다."),
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "파티가 존재하지 않습니다."),

    // PartyMember
    IS_NOT_PARTY_OWNER(HttpStatus.FORBIDDEN, "해당 파티의 파티장이 아닙니다."),
    IS_NOT_PARTY_MEMBER(HttpStatus.FORBIDDEN, "해당 파티의 파티원이 아닙니다."),
    IS_NOT_PARTY_MEMBER_OWN(HttpStatus.FORBIDDEN, "파티원 본인이 아닙니다."),
    IS_ALREADY_PARTY_MEMBER(HttpStatus.FORBIDDEN, "이미 해당 파티의 파티원입니다."),
    IS_PARTY_MEMBER_OWN(HttpStatus.FORBIDDEN, "파티원 본인 스스로에 대한 처리는 불가능합니다."),
    PARTY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "파티 멤버가 존재하지 않습니다."),
    PARTY_OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "파티장이 존재하지 않습니다."),
    PENDING_PARTY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "파티 참가를 신청한 사용자가 아닙니다."),

    // PartyLog
    PARTY_LOG_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 파티 로그를 작성하셨습니다."),
    PARTY_IS_NOT_ENDED(HttpStatus.BAD_REQUEST, "파티가 종료되지 않았습니다."),
    PARTY_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "파티 로그가 존재하지 않습니다."),

    // Tag
    TAG_TYPE_CONVERT_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "태그 타입 변환에 실패하였습니다."),
    TAG_VALUE_CONVERT_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "태그 이름 변환에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public ServiceException throwServiceException() {
        throw new ServiceException(httpStatus, message);
    }

    public ServiceException throwServiceException(Throwable cause) {
        throw new ServiceException(httpStatus, message, cause);
    }
}