package com.sidework.project.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.project.persistence.entity.ProjectLikeEntity;

@Repository
public interface ProjectLikeRepository extends JpaRepository<ProjectLikeEntity, Long> {
	boolean existsByUserIdAndProjectId(Long userId, Long projectId);

	@Modifying
	@Query("""
    DELETE FROM ProjectLikeEntity pl
    WHERE pl.userId = :userId
      AND pl.projectId = :projectId
    """)
	void deleteByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

	@Modifying
	@Query(
		value = """
			INSERT IGNORE INTO project_likes (user_id, project_id, created_at, updated_at)
			VALUES (:userId, :projectId, NOW(), NOW())
			""",
		nativeQuery = true
	)
	int insertIgnore(@Param("userId") Long userId, @Param("projectId") Long projectId);

	@Query("SELECT pl.projectId FROM ProjectLikeEntity pl WHERE pl.userId = :userId AND pl.projectId IN :projectIds")
	List<Long> findProjectIdsByUserIdAndProjectIdIn(@Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);

	@Query("SELECT pl.projectId FROM ProjectLikeEntity pl WHERE pl.userId = :userId")
	List<Long> findProjectIdsByUserId(@Param("userId") Long userId);

}
