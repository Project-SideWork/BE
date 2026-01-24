package com.sidework.profile.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortfolioType {
	INTERN("인턴"),
	CLUB("동아리"),
	PROJECT("프로젝트"),
	EDUCATION("교육");
	private final String value;
}
