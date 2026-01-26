package com.sidework.profile.persistence.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sidework.profile.application.port.out.PortfolioOutPort;
import com.sidework.profile.domain.Portfolio;
import com.sidework.profile.persistence.entity.PortfolioEntity;
import com.sidework.profile.persistence.exception.PortfolioNotFoundException;
import com.sidework.profile.persistence.mapper.PortfolioMapper;
import com.sidework.profile.persistence.repository.PortfolioJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PortfolioPersistenceAdapter implements PortfolioOutPort {

	private final PortfolioJpaRepository portfolioRepository;
	private final PortfolioMapper portfolioMapper;

	@Override
	public Portfolio findById(Long id) {
		PortfolioEntity entity = portfolioRepository.findById(id)
			.orElseThrow(() -> new PortfolioNotFoundException(id));
		return portfolioMapper.toDomain(entity);
	}

	@Override
	public List<Portfolio> findByIdIn(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		List<PortfolioEntity> entities = portfolioRepository.findByIdIn(ids);
		return entities.stream()
			.map(portfolioMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void savePortfolios(List<Portfolio> portfolios) {

	}

	@Override
	public void deletePortfolios(List<Long> ids) {

	}
}
