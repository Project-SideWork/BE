package com.sidework.school.persistence.repository;

import java.util.List;

import com.sidework.school.persistence.entity.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolJpaRepository extends JpaRepository<SchoolEntity, Long> {

	List<SchoolEntity> findByIdIn(List<Long> ids);

	@Query(""" 
    SELECT s 
    FROM SchoolEntity s 
    WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY s.name ASC 
    """)
	List<SchoolEntity> searchByName(@Param("keyword") String keyword);
}

