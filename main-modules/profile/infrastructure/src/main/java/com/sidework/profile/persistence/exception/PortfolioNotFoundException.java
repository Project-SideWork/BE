package com.sidework.profile.persistence.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class PortfolioNotFoundException extends GlobalException {
	public PortfolioNotFoundException(Long id) {
		super(ErrorStatus.PORTFOLIO_NOT_FOUND.withDetail("대외활동 ID: " + id + "를 찾을 수 없습니다."));
	}
}
