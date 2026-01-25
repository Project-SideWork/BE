package com.sidework.profile.persistence.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sidework.profile.application.port.out.SchoolOutPort;
import com.sidework.profile.domain.School;
import com.sidework.profile.persistence.entity.SchoolEntity;
import com.sidework.profile.persistence.exception.SchoolNotFoundException;
import com.sidework.profile.persistence.mapper.SchoolMapper;
import com.sidework.profile.persistence.repository.SchoolJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SchoolPersistenceAdapter implements SchoolOutPort {

	private final SchoolJpaRepository schoolRepository;
	private final SchoolMapper schoolMapper;

	@Override
	public School findById(Long id) {
		SchoolEntity entity = schoolRepository.findById(id)
			.orElseThrow(() -> new SchoolNotFoundException(id));
		return schoolMapper.toDomain(entity);
	}

	@Override
	public List<School> findByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		List<SchoolEntity> entities = schoolRepository.findAllById(ids);
		return entities.stream()
			.map(schoolMapper::toDomain)
			.collect(Collectors.toList());
	}
}
