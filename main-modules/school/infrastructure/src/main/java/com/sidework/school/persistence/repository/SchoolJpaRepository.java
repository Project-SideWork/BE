package com.sidework.school.persistence.repository;

import java.util.List;

import com.sidework.school.persistence.entity.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolJpaRepository extends JpaRepository<SchoolEntity, Long> {

	List<SchoolEntity> findByIdIn(List<Long> ids);
}

