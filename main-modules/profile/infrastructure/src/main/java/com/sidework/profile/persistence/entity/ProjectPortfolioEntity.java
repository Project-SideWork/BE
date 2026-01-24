package com.sidework.profile.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_portfolios")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPortfolioEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "profile_id", nullable = false)
	private Long profileId;

	@Column(name = "portfolio_id", nullable = false)
	private Long portfolioId;
}

