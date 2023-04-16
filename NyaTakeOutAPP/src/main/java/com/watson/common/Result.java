package com.watson.common;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(ResultEnum code, String msg, T data) {
        this.code = code.value;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    public Result setCode(ResultEnum resultEnum) {
        this.code = resultEnum.value;
        return this;
    }

    public static <T> Result<T> success(String msg) {
        return build(ResultEnum.SUCCESS, msg, null);
    }

    public static <T> Result<T> success(T data) {
        return build(ResultEnum.SUCCESS, "", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return build(ResultEnum.SUCCESS, msg, data);
    }

    public static <T> Result<T> success(ResultEnum code, String msg) {
        return build(code, msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return build(ResultEnum.FAIL, msg, null);
    }

    public static <T> Result<T> error(String msg, T data) {
        return build(ResultEnum.FAIL, msg, data);
    }

    public static <T> Result<T> error(ResultEnum code, String msg) {
        return build(code, msg, null);
    }

    public static <T> Result<T> error(T data) {
        return build(ResultEnum.FAIL, "", data);
    }

    public static <T> Result<T> build(ResultEnum code, String msg, T data) {
        Result<T> result = new Result<>();
        result.code = code.value;
        result.msg = msg;
        result.data = data;
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}


