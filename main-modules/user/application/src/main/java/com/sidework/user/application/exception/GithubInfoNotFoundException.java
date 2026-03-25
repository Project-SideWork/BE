package com.sidework.user.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class GithubInfoNotFoundException extends GlobalException {
    public GithubInfoNotFoundException() {
        super(ErrorStatus.GITHUB_INFO_NOT_FOUND);
    }
}
