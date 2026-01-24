package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolJpaRepository extends JpaRepository<SchoolEntity, Long> {
}

