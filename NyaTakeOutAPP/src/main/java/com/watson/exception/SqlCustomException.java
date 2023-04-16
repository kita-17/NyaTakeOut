package com.watson.exception;

import com.watson.common.ResultEnum;

public class SqlCustomException extends RuntimeException {
    private final int code;

    public SqlCustomException(ResultEnum re, String message) {
        super(message);
        code = re.value;
    }

    public int getCode() {
        return code;
    }
}
