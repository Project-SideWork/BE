package com.sidework.school.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sidework.school.application.adapter.SchoolResponse;
import com.sidework.school.application.port.in.SchoolQueryUseCase;
import com.sidework.school.application.port.out.SchoolQueryOutPort;
import com.sidework.school.domain.School;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolQueryService implements SchoolQueryUseCase {

	private final SchoolQueryOutPort schoolQueryOutPort;


	@Override
	public List<School> findByIdIn(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		return schoolQueryOutPort.findByIdIn(ids);
	}

	@Override
	public List<SchoolResponse> findAll() {
		return schoolQueryOutPort.findAll().stream()
			.map(SchoolResponse::from)
			.toList();
	}
}
