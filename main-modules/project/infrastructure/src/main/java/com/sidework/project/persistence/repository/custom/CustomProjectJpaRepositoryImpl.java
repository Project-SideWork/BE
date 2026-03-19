package com.sidework.project.persistence.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sidework.project.persistence.entity.ProjectEntity;
import com.sidework.project.persistence.entity.QProjectEntity;
import com.sidework.project.persistence.entity.QProjectLikeEntity;
import com.sidework.project.persistence.repository.condition.ProjectSearchCondition;
import com.sidework.skill.persistence.entity.QProjectRequiredSkillEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class CustomProjectJpaRepositoryImpl implements CustomProjectJpaRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProjectEntity> searchByKeywordAndSkillIdsQuerydsl(
        ProjectSearchCondition condition,
        Pageable pageable) {

        QProjectEntity project = QProjectEntity.projectEntity;

        JPAQuery<ProjectEntity> contentQuery = queryFactory
            .selectFrom(project)
            .where(
                keywordContains(condition.getKeyword()),
                skillExists(condition.getSkillIds(), condition.getSkillCount())
            )
            .orderBy(project.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());


        List<ProjectEntity> content = contentQuery.fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(project.count())
            .from(project)
            .where(
                keywordContains(condition.getKeyword()),
                skillExists(condition.getSkillIds(), condition.getSkillCount())
            );

        return PageableExecutionUtils.getPage(
            content,
            pageable,
            countQuery::fetchOne
        );
    }

	@Override
	public Page<ProjectEntity> searchByKeywordAndSkillIdsInProjectIdsQuerydsl(ProjectSearchCondition condition, Pageable pageable) {
		QProjectEntity project = QProjectEntity.projectEntity;

		JPAQuery<ProjectEntity> contentQuery = queryFactory
			.selectFrom(project)
			.where(
				projectIdIn(condition.getProjectIds()),
				keywordContains(condition.getKeyword()),
				skillExists(condition.getSkillIds(), condition.getSkillCount())
			)
			.orderBy(project.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		List<ProjectEntity> content = contentQuery.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(project.count())
			.from(project)
			.where(
				projectIdIn(condition.getProjectIds()),
				keywordContains(condition.getKeyword()),
				skillExists(condition.getSkillIds(), condition.getSkillCount())
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

	@Override
	public Page<ProjectEntity> searchLikedByKeywordAndSkillIdsQuerydsl(Long userId, ProjectSearchCondition condition, Pageable pageable) {
		QProjectEntity project = QProjectEntity.projectEntity;
		QProjectLikeEntity projectLike = QProjectLikeEntity.projectLikeEntity;

		JPAQuery<ProjectEntity> contentQuery = queryFactory
			.selectFrom(project)
			.join(projectLike).on(
				projectLike.projectId.eq(project.id)
					.and(projectLike.userId.eq(userId))
			)
			.where(
				keywordContains(condition.getKeyword()),
				skillExists(condition.getSkillIds(), condition.getSkillCount())
			)
			.orderBy(project.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		List<ProjectEntity> content = contentQuery.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(project.countDistinct())
			.from(project)
			.join(projectLike).on(
				projectLike.projectId.eq(project.id)
					.and(projectLike.userId.eq(userId))
			)
			.where(
				keywordContains(condition.getKeyword()),
				skillExists(condition.getSkillIds(), condition.getSkillCount())
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

        QProjectEntity project = QProjectEntity.projectEntity;

        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return project.title.containsIgnoreCase(keyword)
            .or(project.description.containsIgnoreCase(keyword));
    }

    private BooleanExpression skillExists(List<Long> skillIds, long skillCount) {

        if (skillIds == null || skillIds.isEmpty()) {
            return null;
        }

        QProjectRequiredSkillEntity prs = QProjectRequiredSkillEntity.projectRequiredSkillEntity;
        QProjectEntity project = QProjectEntity.projectEntity;

        return JPAExpressions
            .select(prs.projectId)
            .from(prs)
            .where(
                prs.projectId.eq(project.id)
                    .and(prs.skillId.in(skillIds))
            )
            .groupBy(prs.projectId)
            .having(prs.skillId.countDistinct().eq(skillCount))
            .exists();
    }

	private BooleanExpression projectIdIn(List<Long> projectIds) {
		if (projectIds == null || projectIds.isEmpty()) {
			return null;
		}
		QProjectEntity project = QProjectEntity.projectEntity;
		return project.id.in(projectIds);
	}

}