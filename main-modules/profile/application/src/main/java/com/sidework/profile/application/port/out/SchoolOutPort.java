package com.sidework.profile.application.port.out;

import java.util.List;

import com.sidework.profile.domain.School;

public interface SchoolOutPort
{
	School findById(Long id);
	List<School> findByIdIn(List<Long> ids);
}
