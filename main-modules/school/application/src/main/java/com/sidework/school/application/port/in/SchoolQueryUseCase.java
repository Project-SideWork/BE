package com.sidework.school.application.port.in;

import java.util.List;

import com.sidework.school.application.adapter.SchoolResponse;
import com.sidework.school.domain.School;

public interface SchoolQueryUseCase {

	List<School> findByIdIn(List<Long> ids);
	List<SchoolResponse> findAll();
	List<SchoolResponse> searchByName(String name);
}

