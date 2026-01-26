package com.sidework.profile.application.port.out;

import java.util.List;

import com.sidework.profile.domain.Portfolio;

public interface PortfolioOutPort
{
	Portfolio findById(Long id);
	List<Portfolio> findByIdIn(List<Long> ids);
}
