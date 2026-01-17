package com.sidework.common.response;

import com.sidework.common.response.status.BaseStatusCode;
import org.springframework.http.HttpStatus;

public record ErrorDetail(HttpStatus httpStatus, String code, String message) {

    public static ErrorDetail from(BaseStatusCode code) {
        return new ErrorDetail(
                code.getHttpStatus(),
                code.getCode(),
                code.getMessage()
        );
    }
}
