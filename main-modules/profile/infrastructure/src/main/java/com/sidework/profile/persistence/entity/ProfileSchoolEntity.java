package com.sidework.profile.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import com.sidework.profile.domain.SchoolStateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "profile_schools")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSchoolEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "profile_id", nullable = false)
	private Long profileId;

	@Column(name = "school_id", nullable = false)
	private Long schoolId;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false, length = 30)
	private SchoolStateType state;

	@Column(name = "major", length = 20)
	private String major;

	@Column(name = "start_dt", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_dt", nullable = true)
	private LocalDate endDate;
}

