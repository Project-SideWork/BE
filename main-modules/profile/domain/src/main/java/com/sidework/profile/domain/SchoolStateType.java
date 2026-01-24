package com.sidework.profile.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchoolStateType {

	ENROLLED("재학중"),
	LEAVE_OF_ABSENCE("휴학"),
	TRANSFER("편입"),
	GRADUATED("졸업"),
	DROPPED_OUT("중퇴"),
	COMPLETED("수료");

	private final String value;
}
