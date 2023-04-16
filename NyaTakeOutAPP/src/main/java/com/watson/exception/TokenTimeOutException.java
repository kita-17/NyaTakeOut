package com.watson.exception;

public class TokenTimeOutException extends RuntimeException {
    public TokenTimeOutException(String msg) {
        super(msg);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
