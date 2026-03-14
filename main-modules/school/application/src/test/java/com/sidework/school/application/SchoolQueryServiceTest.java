package com.sidework.school.application;

import com.sidework.school.application.port.in.SchoolQueryUseCase;
import com.sidework.school.application.port.out.SchoolQueryOutPort;
import com.sidework.school.application.service.SchoolQueryService;
import com.sidework.school.domain.School;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchoolQueryServiceTest {

	@Mock
	private SchoolQueryOutPort schoolQueryOutPort;

	@InjectMocks
	private SchoolQueryService schoolQueryService;

	@Test
	void ids가_null이면_빈_리스트를_반환한다() {
		// when
		List<School> result = schoolQueryService.findByIdIn(null);

		// then
		assertTrue(result.isEmpty());
	}

	@Test
	void ids가_비어있으면_빈_리스트를_반환한다() {
		// when
		List<School> result = schoolQueryService.findByIdIn(List.of());

		// then
		assertTrue(result.isEmpty());
	}

	@Test
	void ids로_학교목록을_조회한다() {
		// given
		List<Long> ids = List.of(1L, 2L);
		List<School> schools = List.of(
			School.builder().id(1L).name("학교1").build(),
			School.builder().id(2L).name("학교2").build()
		);
		when(schoolQueryOutPort.findByIdIn(ids)).thenReturn(schools);

		// when
		List<School> result = schoolQueryService.findByIdIn(ids);

		// then
		assertEquals(2, result.size());
		verify(schoolQueryOutPort).findByIdIn(ids);
	}
}

