package com.sidework.user.persistence.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class UserNotFoundException extends GlobalException {
    public UserNotFoundException(Long id) {
        super(ErrorStatus.USER_NOT_FOUND.withDetail(String.format("사용자(id=%d)를 찾을 수 없습니다.", id)));
    }

    public UserNotFoundException(String email) {
        super(ErrorStatus.USER_NOT_FOUND.withDetail(String.format("사용자(email=%d)를 찾을 수 없습니다.", email)));
    }
}
