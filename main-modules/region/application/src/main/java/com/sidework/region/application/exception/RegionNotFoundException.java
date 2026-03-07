package com.sidework.region.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class RegionNotFoundException extends GlobalException {
    public RegionNotFoundException(Long id) {
        super(ErrorStatus.REGION_NOT_FOUND.withDetail((String.format("지역(id=%d)를 찾을 수 없습니다.", id))));
    }
}
