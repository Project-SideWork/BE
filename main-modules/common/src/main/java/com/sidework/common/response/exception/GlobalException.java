package com.sidework.common.response.exception;

import com.sidework.common.response.ErrorDetail;
import com.sidework.common.response.status.BaseStatusCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final BaseStatusCode code;

    public GlobalException(BaseStatusCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public ErrorDetail getErrorDetail() {
        return ErrorDetail.from(this.code);
    }
}
