package com.sidework.profile.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import com.sidework.profile.domain.PortfolioType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "portfolios")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 10)
	private PortfolioType type;

	@Column(name = "start_dt", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_dt", nullable = false)
	private LocalDate endDate;

	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;
}

