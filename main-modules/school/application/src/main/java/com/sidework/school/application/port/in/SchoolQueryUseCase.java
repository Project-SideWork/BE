package com.sidework.school.application.port.in;

import java.util.List;

import com.sidework.school.domain.School;

public interface SchoolQueryUseCase {

	School findById(Long id);

	List<School> findByIdIn(List<Long> ids);
}

