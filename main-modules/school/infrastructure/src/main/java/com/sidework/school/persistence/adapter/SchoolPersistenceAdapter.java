package com.sidework.school.persistence.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sidework.school.application.exception.SchoolNotFoundException;
import com.sidework.school.application.port.in.SchoolQueryUseCase;
import com.sidework.school.domain.School;
import com.sidework.school.persistence.entity.SchoolEntity;
import com.sidework.school.persistence.mapper.SchoolMapper;
import com.sidework.school.persistence.repository.SchoolJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SchoolPersistenceAdapter implements SchoolQueryUseCase {

	private final SchoolJpaRepository schoolRepository;
	private final SchoolMapper schoolMapper;

	@Override
	public School findById(Long id) {
		SchoolEntity entity = schoolRepository.findById(id)
			.orElseThrow(() -> new SchoolNotFoundException(id));
		return schoolMapper.toDomain(entity);
	}

	@Override
	public List<School> findByIdIn(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		List<SchoolEntity> entities = schoolRepository.findByIdIn(ids);
		return entities.stream()
			.map(schoolMapper::toDomain)
			.collect(Collectors.toList());
	}
}

