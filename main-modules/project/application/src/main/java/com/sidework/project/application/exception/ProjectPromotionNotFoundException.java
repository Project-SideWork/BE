package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectPromotionNotFoundException extends GlobalException {
	public ProjectPromotionNotFoundException(Long promotionId) {
		super(ErrorStatus.PROJECT_PROMOTION_NOT_FOUND.withDetail("홍보글 아이디: "+ promotionId + "인 프로젝트 홍보글을 찾을 수 없습니다."));
	}
}
