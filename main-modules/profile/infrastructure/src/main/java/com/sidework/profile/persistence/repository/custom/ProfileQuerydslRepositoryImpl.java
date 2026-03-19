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
import com.sidework.profile.persistence.entity.QProfileLikeEntity;
import com.sidework.profile.persistence.entity.QProfileSkillEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProfileQuerydslRepositoryImpl implements ProfileQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ProfileEntity> searchProfilesBySkillIds(List<Long> skillIds, Pageable pageable) {
		QProfileEntity profile = QProfileEntity.profileEntity;
		QProfileSkillEntity profileSkill = QProfileSkillEntity.profileSkillEntity;

		BooleanExpression predicate = skillIdIn(profileSkill, skillIds);

		JPAQuery<ProfileEntity> contentQuery = queryFactory
			.select(profile)
			.distinct()
			.from(profile)
			.leftJoin(profileSkill).on(profileSkill.profileId.eq(profile.id))
			.where(predicate)
			.orderBy(profile.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		List<ProfileEntity> content = contentQuery.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(profile.id.countDistinct())
			.from(profile)
			.leftJoin(profileSkill).on(profileSkill.profileId.eq(profile.id))
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

	@Override
	public Page<ProfileEntity> searchLikedProfilesBySkillIds(Long userId, List<Long> skillIds, Pageable pageable) {
		QProfileEntity profile = QProfileEntity.profileEntity;
		QProfileLikeEntity profileLike = QProfileLikeEntity.profileLikeEntity;
		QProfileSkillEntity profileSkill = QProfileSkillEntity.profileSkillEntity;

		BooleanExpression predicate = skillIdIn(profileSkill, skillIds);

		JPAQuery<ProfileEntity> contentQuery = queryFactory
			.select(profile)
			.distinct()
			.from(profile)
			.join(profileLike).on(
				profileLike.profileId.eq(profile.id)
					.and(profileLike.userId.eq(userId))
			)
			.leftJoin(profileSkill).on(profileSkill.profileId.eq(profile.id))
			.where(predicate)
			.orderBy(profile.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		List<ProfileEntity> content = contentQuery.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(profile.id.countDistinct())
			.from(profile)
			.join(profileLike).on(
				profileLike.profileId.eq(profile.id)
					.and(profileLike.userId.eq(userId))
			)
			.leftJoin(profileSkill).on(profileSkill.profileId.eq(profile.id))
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

	private BooleanExpression skillIdIn(QProfileSkillEntity profileSkill, List<Long> skillIds) {
		if (skillIds == null || skillIds.isEmpty()) {
			return null;
		}
		return profileSkill.skillId.in(skillIds);
	}

}

