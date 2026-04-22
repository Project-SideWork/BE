package com.sidework.project.persistence.repository;

import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.persistence.entity.ProjectEntity;
import com.sidework.project.persistence.repository.custom.CustomProjectJpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long>, CustomProjectJpaRepository {
	@Query(
		"""
		SELECT new com.sidework.project.application.dto.ProjectTitleDto(p.id, p.title) FROM ProjectEntity p
		WHERE p.id IN (:ids)
		"""
	)
	List<ProjectTitleDto> findProjectionsByIds(@Param("ids") List<Long> ids);

	@Query(
		"""
		SELECT p
		FROM ProjectEntity p
		WHERE
			(:keyword IS NULL OR :keyword = '')
			OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
		"""
	)
	Page<ProjectEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	@Query("""
    SELECT p.status
    FROM ProjectEntity p
    WHERE p.id = :projectId
    """)
	ProjectStatus findStatusById(@Param("projectId") Long projectId);

    @Query("""
            SELECT p FROM ProjectEntity p
            WHERE p.id in :ids
            ORDER BY p.id DESC
            """)

    List<ProjectEntity> findAllByIdsInDesc(@Param("ids")List<Long> projectIds);
}
