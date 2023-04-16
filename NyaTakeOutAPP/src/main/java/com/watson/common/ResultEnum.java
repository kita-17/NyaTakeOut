package com.watson.common;

public enum ResultEnum {
    /**
     * 成功
     */
    SUCCESS(200),
    /**
     * 失败
     */
    FAIL(400),
    /**
     * 接口不存在
     */
    NOT_FOUND(404),
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500),

    // ---------------- 账号相关 ----------------
    /**
     * 账号登录Token超时, 失效
     */
    TOKEN_TIME_OUT(1001),

    // ---------------- 数据库异常相关 ----------------
    /**
     * 数据重复
     */
    SQL_INTEGRITY_CONSTRAINT_VIOLATION(2001),

    // ---------------- 业务异常相关 ----------------
    CATEGORY_ADD_SUCCESS(3000),
    CATEGORY_ADD_FAIL(3001),
    CATEGORY_DELETE_SUCCESS(3002),
    CATEGORY_DELETE_FAIL(3003),
    CATEGORY_UPDATE_SUCCESS(3004),
    CATEGORY_UPDATE_FAIL(3005);
    public final int value;

    ResultEnum(int code) {
        this.value = code;
    }
}
