package com.sidework.school.application.port.out;

import java.util.List;

import com.sidework.school.domain.School;

public interface SchoolQueryOutPort {
	List<School> findByIdIn(List<Long> ids);
	List<School> findAll();
	List<School> searchByName(String keyword);
}
