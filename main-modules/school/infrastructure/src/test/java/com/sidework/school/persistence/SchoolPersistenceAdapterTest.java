package com.sidework.school.persistence;

import com.sidework.school.application.exception.SchoolNotFoundException;
import com.sidework.school.application.port.out.SchoolQueryOutPort;
import com.sidework.school.domain.School;
import com.sidework.school.persistence.adapter.SchoolPersistenceAdapter;
import com.sidework.school.persistence.entity.SchoolEntity;
import com.sidework.school.persistence.mapper.SchoolMapper;
import com.sidework.school.persistence.repository.SchoolJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchoolPersistenceAdapterTest {

	@Mock
	private SchoolJpaRepository schoolRepository;

	private SchoolMapper schoolMapper = Mappers.getMapper(SchoolMapper.class);

	private SchoolQueryOutPort adapter;

	@BeforeEach
	void setUp() {
		adapter = new SchoolPersistenceAdapter(schoolRepository, schoolMapper);
	}

	@Test
	void findByIdIn은_ids가_null이면_빈_리스트를_반환한다() {
		// when
		List<School> result = adapter.findByIdIn(null);

		// then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void findByIdIn은_ids가_비어있으면_빈_리스트를_반환한다() {
		// when
		List<School> result = adapter.findByIdIn(List.of());

		// then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void findByIdIn은_엔티티를_도메인으로_매핑해_반환한다() {
		// given
		List<Long> ids = List.of(1L, 2L);
		List<SchoolEntity> entities = List.of(
			createSchoolEntity(1L, "학교1"),
			createSchoolEntity(2L, "학교2")
		);
		when(schoolRepository.findByIdIn(ids)).thenReturn(entities);

		// when
		List<School> result = adapter.findByIdIn(ids);

		// then
		assertEquals(2, result.size());
		assertEquals("학교1", result.get(0).getName());
		assertEquals("학교2", result.get(1).getName());
		verify(schoolRepository).findByIdIn(ids);
	}

	private SchoolEntity createSchoolEntity(Long id, String name) {
		return SchoolEntity.builder()
			.id(id)
			.name(name)
			.address("주소")
			.build();
	}
}

