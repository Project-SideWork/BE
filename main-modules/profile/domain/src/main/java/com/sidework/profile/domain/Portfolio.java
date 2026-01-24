package com.sidework.profile.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
	private Long id;
	private PortfolioType type;
	private LocalDate startDate;
	private LocalDate endDate;
	private String content;
}
