package com.sidework.project.persistence.repository.custom;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.project.persistence.entity.ProjectUserReviewEntity;

@Repository
public interface ProjectUserReviewJpaRepository extends JpaRepository<ProjectUserReviewEntity, Long> {
	boolean existsByProjectIdAndReviewerUserIdAndRevieweeUserId(Long projectId, Long reviewerId, Long revieweeUserId);

    @Query("""
    SELECT r
    FROM ProjectUserReviewEntity r
    WHERE r.projectId IN :projectIds
      AND r.revieweeUserId = :userId
    """)
	List<ProjectUserReviewEntity> findAllByProjectIdsAndRevieweeUserId(
		@Param("projectIds") List<Long> projectIds,
		@Param("userId") Long userId
	);

    @Query("""
    SELECT r
    FROM ProjectUserReviewEntity r
    WHERE r.revieweeUserId = :userId
    ORDER BY r.id DESC
    """)
	List<ProjectUserReviewEntity> findAllByRevieweeUserId(
		@Param("userId") Long userId,
        Pageable pageable
	);



    @Query("""
    SELECT COUNT(DISTINCT pur.id)
    FROM ProjectUserReviewEntity pur
    WHERE pur.revieweeUserId = :userId
    """)
    Long findReviewCountByUserId(@Param("userId")Long userId);

    @Query("""
    SELECT r
    FROM ProjectUserReviewEntity r
    WHERE r.revieweeUserId = :userId
    """)
    List<ProjectUserReviewEntity> findReviewByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );


}
