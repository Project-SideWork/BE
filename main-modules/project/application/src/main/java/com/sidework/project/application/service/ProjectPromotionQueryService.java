package com.sidework.project.application.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.ProjectPromotionDetailResponse;
import com.sidework.project.application.adapter.ProjectPromotionListResponse;
import com.sidework.project.application.port.in.ProjectPromotionQueryUseCase;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewStatOutPort;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.domain.ProjectUser;
import com.sidework.project.domain.ProjectUserReviewStat;
import com.sidework.region.application.port.in.RegionQueryUseCase;
import com.sidework.skill.application.port.in.ProjectPromotionSkillQueryUseCase;
import com.sidework.user.application.port.in.UserQueryUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectPromotionQueryService implements ProjectPromotionQueryUseCase {

	private final ProjectPromotionOutPort projectPromotionOutPort;
	private final ProjectOutPort projectOutPort;
	private final ProjectUserOutPort projectUserOutPort;
	private final ProjectUserReviewStatOutPort projectUserReviewStatOutPort;

	private final ProjectPromotionSkillQueryUseCase projectPromotionSkillQueryUseCase;
	private final RegionQueryUseCase regionQueryUseCase;
	private final UserQueryUseCase userQueryUseCase;

	@Override
	public PageResponse<List<ProjectPromotionListResponse>> queryProjectPromotionList(String keyword, List<Long> skillIds, Pageable pageable) {
		Page<ProjectPromotionListResponse> page = projectPromotionOutPort.search(keyword, skillIds, pageable);
		return PageResponse.from(page, page.getContent());
	}

	@Override
	public ProjectPromotionDetailResponse queryProjectPromotionDetail(Long promotionId, Long projectId) {
		ProjectPromotion promotion = loadPromotionOrThrow(promotionId);

		Project project = loadProjectOrThrow(promotion.getProjectId());

		List<String> usedSkillNames = loadUsedSkillNames(promotion.getId());
		String meetingPlace = resolveMeetingPlace(project.getMeetRegionId());
		Integer durationMonths = resolveDurationMonths(project.getStartDt(), project.getEndDt());
		List<ProjectPromotionDetailResponse.ProjectMemberResponse> teamMembers = buildTeamMembers(project.getId());

		return toDetailResponse(project, promotion, usedSkillNames, meetingPlace, durationMonths, teamMembers);
	}

	private ProjectPromotion loadPromotionOrThrow(Long promotionId) {
		return projectPromotionOutPort.findById(promotionId);
	}

	private Project loadProjectOrThrow(Long projectId) {
		return projectOutPort.findById(projectId);
	}

	private List<String> loadUsedSkillNames(Long promotionId) {
		return projectPromotionSkillQueryUseCase.queryNamesByPromotionId(promotionId);
	}

	private List<ProjectPromotionDetailResponse.ProjectMemberResponse> buildTeamMembers(Long projectId) {
		List<ProjectUser> members = projectUserOutPort.findAllByProjectId(projectId);
		List<Long> userIds = members.stream()
			.map(ProjectUser::getUserId)
			.distinct()
			.toList();

		Map<Long, Double> scoreByUserId = buildAverageReviewScoresByUserIds(userIds);
		Map<Long, String> userNameByUserId = buildUserNameByUserIds(userIds);

		return members.stream()
			.map(m -> new ProjectPromotionDetailResponse.ProjectMemberResponse(
				m.getUserId(),
				m.getProfileId(),
				userNameByUserId.get(m.getUserId()),
				m.getRole(),
				m.getStatus(),
				scoreByUserId.get(m.getUserId())
			))
			.toList();
	}

	private ProjectPromotionDetailResponse toDetailResponse(
		Project project,
		ProjectPromotion promotion,
		List<String> usedSkillNames,
		String meetingPlace,
		Integer durationMonths,
		List<ProjectPromotionDetailResponse.ProjectMemberResponse> teamMembers
	) {
		return new ProjectPromotionDetailResponse(
			project.getId(),
			promotion.getId(),
			project.getTitle(),
			promotion.getDescription(),
			project.getMeetingType(),
			usedSkillNames,
			meetingPlace,
			durationMonths,
			teamMembers
		);
	}

	private Integer resolveDurationMonths(LocalDate startDt, LocalDate endDt) {
		if (startDt == null || endDt == null) {
			return null;
		}
		if (endDt.isBefore(startDt)) {
			return null;
		}
		return (int) ChronoUnit.MONTHS.between(startDt, endDt);
	}

	private String resolveMeetingPlace(Long meetRegionId) {
		if (meetRegionId == null) {
			return null;
		}
		return regionQueryUseCase.getRegion(meetRegionId);
	}

	private Map<Long, Double> buildAverageReviewScoresByUserIds(List<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return Map.of();
		}
		return projectUserReviewStatOutPort.getAllReviewStatsByUserIds(userIds).stream()
			.collect(Collectors.toMap(
				ProjectUserReviewStat::getUserId,
				stat -> stat.getRatingScore() / stat.getRatingCount()
			));
	}

	private Map<Long, String> buildUserNameByUserIds(List<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return Map.of();
		}
		return userQueryUseCase.findNamesByUserIds(userIds);
	}
}
