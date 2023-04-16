package com.watson.exception;

public class AccountException extends RuntimeException{
    private final int code;
    public AccountException(int value, String message) {
        super(message);
        code = value;
    }

    public int getCode() {
        return code;
    }
}
