package com.watson.exception;

import com.watson.common.Result;
import com.watson.common.ResultEnum;
import org.apache.el.parser.Token;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> onSqlException(SQLIntegrityConstraintViolationException exception) {
        return new Result<>(ResultEnum.SQL_INTEGRITY_CONSTRAINT_VIOLATION, exception.getMessage(), null);
    }

    @ExceptionHandler(SetmealException.class)
    public Result<String> onSetmealException(SetmealException exception) {
        return new Result<>(ResultEnum.FAIL, exception.getMessage(), null);
    }
    @ExceptionHandler(SqlCustomException.class)
    public Result<String> onSqlCustomException(SqlCustomException exception) {
        return new Result<>(ResultEnum.FAIL, exception.getMessage(), null);
    }

    @ExceptionHandler(TokenTimeOutException.class)
    public Result<String> onTokenTimeOutException(TokenTimeOutException tokenTimeOutException) {
        return new Result<>(ResultEnum.TOKEN_TIME_OUT, tokenTimeOutException.getMessage(),null);
    }

    //@ExceptionHandler(Exception.class)
    //public Result<String> onSystemException(Exception e) {
    //    return new Result<>(ResultEnum.INTERNAL_SERVER_ERROR, e.getMessage(), null);
    //}
}
