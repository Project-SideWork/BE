package com.sidework.profile.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSchool
{
	private Long id;
	private Long profileId;
	private Long schoolId;
	private SchoolStateType state;
	private String major;
	private LocalDate startDate;
	private LocalDate endDate;

	public static ProfileSchool create(
		Long profileId,
		Long schoolId,
		SchoolStateType state,
		String major,
		LocalDate startDt,
		LocalDate endDt
		) {
		return ProfileSchool.builder()
			.profileId(profileId)
			.schoolId(schoolId)
			.state(state)
			.major(major)
			.startDate(startDt)
			.endDate(endDt).build();
	}
}
