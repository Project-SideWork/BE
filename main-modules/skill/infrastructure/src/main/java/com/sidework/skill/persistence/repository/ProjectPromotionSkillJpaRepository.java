package com.sidework.skill.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.skill.persistence.entity.ProjectPromotionSkillEntity;

@Repository
public interface ProjectPromotionSkillJpaRepository extends JpaRepository<ProjectPromotionSkillEntity, Long> {

	@Query("""
		SELECT p.skillId FROM ProjectPromotionSkillEntity p
		WHERE p.promotionId = :promotionId
		""")
	List<Long> findAllSkillIdsByPromotionId(@Param("promotionId") Long promotionId);

	@Modifying
	@Query("""
		DELETE FROM ProjectPromotionSkillEntity p
		WHERE p.promotionId = :promotionId AND p.skillId IN :ids
		""")
	void deleteByPromotionIdAndSkillIdIn(@Param("promotionId") Long promotionId, @Param("ids") List<Long> ids);
}
