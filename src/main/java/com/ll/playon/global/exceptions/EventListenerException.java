package com.ll.playon.global.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EventListenerException extends RuntimeException {
    private final HttpStatus resultCode;
    private final String msg;

    public EventListenerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.resultCode = errorCode.getHttpStatus();
        this.msg = errorCode.getMessage();
    }

    public EventListenerException(HttpStatus resultCode, String msg) {
        super(msg);
        this.resultCode = resultCode;
        this.msg = msg;
    }
}
