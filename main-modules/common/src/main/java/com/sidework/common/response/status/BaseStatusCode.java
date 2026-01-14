package com.sidework.common.response.status;

import org.springframework.http.HttpStatus;

public interface BaseStatusCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
