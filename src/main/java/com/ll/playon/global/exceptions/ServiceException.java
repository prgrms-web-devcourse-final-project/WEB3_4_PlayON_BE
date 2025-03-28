package com.ll.playon.global.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {

    private final HttpStatus resultCode;
    private final String msg;

    public ServiceException(HttpStatus resultCode, String msg) {
        super(resultCode + " : " + msg);
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public ServiceException(HttpStatus resultCode, String msg, Throwable cause) {
        super(resultCode + " : " + msg, cause);
        this.resultCode = resultCode;
        this.msg = msg;
    }
}
