package com.sidework.profile.persistence.repository.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sidework.profile.persistence.entity.ProfileEntity;
import com.sidework.profile.persistence.entity.QProfileEntity;
import com.sidework.profile.persistence.entity.QProfileSkillEntity;
import com.sidework.skill.persistence.entity.QSkillEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProfileQuerydslRepositoryImpl implements ProfileQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ProfileEntity> searchProfilesBySkillNames(List<String> keywords, Pageable pageable) {
		QProfileEntity profile = QProfileEntity.profileEntity;
		QProfileSkillEntity profileSkill = QProfileSkillEntity.profileSkillEntity;
		QSkillEntity skill = QSkillEntity.skillEntity;

		BooleanExpression predicate = skillNameContainsAny(skill, keywords);

		JPAQuery<ProfileEntity> contentQuery = queryFactory
			.select(profile)
			.distinct()
			.from(profile)
			.leftJoin(profileSkill).on(profileSkill.profileId.eq(profile.id))
			.leftJoin(skill).on(skill.id.eq(profileSkill.skillId))
			.where(predicate)
			.orderBy(profile.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		List<ProfileEntity> content = contentQuery.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(profile.id.countDistinct())
			.from(profile)
			.leftJoin(profileSkill).on(profileSkill.profileId.eq(profile.id))
			.leftJoin(skill).on(skill.id.eq(profileSkill.skillId))
			.where(predicate);

		return PageableExecutionUtils.getPage(
			content,
			pageable,
			() -> {
				Long total = countQuery.fetchOne();
				return total == null ? 0L : total;
			}
		);
	}

	private BooleanExpression skillNameContainsAny(QSkillEntity skill, List<String> keywords) {
		if (keywords == null || keywords.isEmpty()) {
			return null;
		}
		BooleanExpression expr = null;
		for (String kw : keywords) {
			if (kw == null || kw.isBlank()) {
				continue;
			}
			BooleanExpression one = skill.name.containsIgnoreCase(kw.trim());
			expr = (expr == null) ? one : expr.or(one);
		}
		return expr;
	}
}

