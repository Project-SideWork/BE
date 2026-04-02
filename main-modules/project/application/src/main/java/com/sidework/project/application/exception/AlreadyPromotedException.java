package com.sidework.project.application.exception;

import org.springframework.http.HttpStatus;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class AlreadyPromotedException extends GlobalException {
	public AlreadyPromotedException() {
		super(ErrorStatus.PROJECT_PROMOTION_ALREADY_EXISTS);
	}
}
