package com.ll.playon.global.validation;

import com.ll.playon.global.exceptions.ErrorCode;

public class GlobalValidation {
    public static void checkPageSize(int pageSize) {
        if (pageSize < 1 || pageSize > 100) {
            ErrorCode.PAGE_SIZE_LIMIT_EXCEEDED.throwServiceException();
        }
    }
}
