package com.sidework.school.application.adapter;

import com.sidework.school.application.port.in.SchoolQueryUseCase;
import com.sidework.school.domain.School;

public record SchoolResponse(
	Long schoolId,
	String name
)
{
	public static SchoolResponse from(School school) {
		return new SchoolResponse(school.getId(), school.getName());
	}
}
