package com.sidework.region.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class InvalidRegionLevelException extends GlobalException {
    public InvalidRegionLevelException() {
        super(ErrorStatus.REGION_INVALID_LEVEL);
    }
}
