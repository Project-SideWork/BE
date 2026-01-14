package com.sidework.common.response.status;

import org.springframework.http.HttpStatus;

public enum SuccessStatus implements BaseStatusCode {

    OK(HttpStatus.OK, "COMMON_200", "요청이 정상적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON_201", "데이터가 정상적으로 생성되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    SuccessStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }


    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}