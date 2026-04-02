package com.sidework.project.persistence.repository.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sidework.project.application.dto.ProjectPromotionListRow;
import com.sidework.project.persistence.entity.QProjectEntity;
import com.sidework.project.persistence.entity.QProjectPromotionEntity;
import com.sidework.project.persistence.repository.condition.ProjectPromotionSearchCondition;
import com.sidework.skill.persistence.entity.QProjectPromotionSkillEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomProjectPromotionJpaRepositoryImpl implements CustomProjectPromotionJpaRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ProjectPromotionListRow> searchPromotions(ProjectPromotionSearchCondition condition, Pageable pageable) {
		QProjectPromotionEntity pp = QProjectPromotionEntity.projectPromotionEntity;
		QProjectEntity p = QProjectEntity.projectEntity;

		JPAQuery<ProjectPromotionListRow> contentQuery = queryFactory
			.select(Projections.constructor(ProjectPromotionListRow.class,
				p.id,
				p.title,
				pp.description,
				pp.id))
			.from(pp)
			.join(p).on(pp.projectId.eq(p.id))
			.where(
				keywordContains(condition.getKeyword()),
				promotionSkillExists(condition.getSkillIds(), condition.getSkillCount())
			)
			.orderBy(pp.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		List<ProjectPromotionListRow> content = contentQuery.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(pp.count())
			.from(pp)
			.join(p).on(pp.projectId.eq(p.id))
			.where(
				keywordContains(condition.getKeyword()),
				promotionSkillExists(condition.getSkillIds(), condition.getSkillCount())
			);

		return PageableExecutionUtils.getPage(
			content,
			pageable,
			() -> {
				Long total = countQuery.fetchOne();
				return total == null ? 0L : total;
			}
		);
	}

	private BooleanExpression keywordContains(String keyword) {
		QProjectEntity p = QProjectEntity.projectEntity;
		QProjectPromotionEntity pp = QProjectPromotionEntity.projectPromotionEntity;

		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		return p.title.containsIgnoreCase(keyword)
			.or(pp.description.containsIgnoreCase(keyword));
	}

	private BooleanExpression promotionSkillExists(List<Long> skillIds, long skillCount) {
		if (skillIds == null || skillIds.isEmpty()) {
			return null;
		}

		QProjectPromotionSkillEntity pps = QProjectPromotionSkillEntity.projectPromotionSkillEntity;
		QProjectPromotionEntity pp = QProjectPromotionEntity.projectPromotionEntity;

		return JPAExpressions
			.select(pps.promotionId)
			.from(pps)
			.where(
				pps.promotionId.eq(pp.id)
					.and(pps.skillId.in(skillIds))
			)
			.groupBy(pps.promotionId)
			.having(pps.skillId.countDistinct().eq(skillCount))
			.exists();
	}
}
