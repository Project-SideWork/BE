package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioJpaRepository extends JpaRepository<PortfolioEntity, Long>
{

}
