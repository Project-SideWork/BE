package com.sidework.profile.application.service;

import com.sidework.profile.application.adapter.UserProfileListResponse;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.region.application.port.in.RegionQueryUseCase;
import com.sidework.school.application.port.in.SchoolQueryUseCase;
import com.sidework.school.domain.School;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.application.service.ProjectRequiredSkillQueryService;
import com.sidework.skill.domain.Skill;
import com.sidework.common.response.PageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.profile.application.adapter.UserProfileResponse;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;
import com.sidework.profile.application.port.in.ProfileLikeQueryUseCase;
import com.sidework.profile.application.port.out.PortfolioOutPort;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.application.port.out.RoleOutPort;
import com.sidework.profile.domain.Portfolio;
import com.sidework.profile.domain.Role;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.domain.Project;
import com.sidework.user.application.port.in.UserQueryUseCase;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.domain.ProjectPortfolio;
import com.sidework.user.domain.User;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileQueryService implements ProfileQueryUseCase
{
	private final ProfileOutPort profileRepository;
	private final SkillOutPort skillRepository;
	private final PortfolioOutPort portfolioRepository;
	private final RoleOutPort roleRepository;

	private final UserQueryUseCase userQueryUseCase;
	private final ProjectQueryUseCase projectQueryUseCase;
	private final ProjectRequiredSkillQueryService requiredSkillUseCase;
	private final RegionQueryUseCase regionQueryUseCase;
	private final SchoolQueryUseCase schoolQueryUseCase;
	private final ProfileLikeQueryUseCase profileLikeQueryUseCase;

	@Override
	public UserProfileResponse getProfileByUserId(Long userId) {

		User user = userQueryUseCase.findById(userId);

		List<Project> projects = projectQueryUseCase.queryByUserId(userId);
		int projectCounts = countCompletedProjects(projects);

		Profile profile = profileRepository.getProfileByUserId(userId);
		if (profile == null) {
			return buildResponseWhenNoProfile(user, projects, projectCounts);
		}

		List<Long> projectIds = projects.stream().map(Project::getId).toList();
		Map<Long, List<String>> skillNamesByProjectId = requiredSkillUseCase.queryNamesByProjectIds(projectIds);
		Map<Long, List<ProjectRole>> rolesByProjectId = projectQueryUseCase.queryUserRolesByProjects(userId, projectIds);

		String residenceText = buildResidenceText(user.getResidenceRegionId());
		return new UserProfileResponse(
			user.getId(),
			user.getEmail(),
			user.getName(),
			user.getNickname(),
			user.getAge(),
			user.getTel(),
			profile.getId(),
			profile.getSelfIntroduction(),
			residenceText,
			projectCounts,
			buildRoleInfos(profile.getId()),
			buildSchoolInfos(profile.getId()),
			buildSkillInfos(profile.getId()),
			buildPortfolioInfos(profile.getId()),
			buildProjectInfos(projects, skillNamesByProjectId, rolesByProjectId)
		);
	}

	@Override
	public boolean existsByIdAndUserId(Long profileId, Long userId) {
		return profileRepository.existsByIdAndUserId(profileId, userId);
	}

	private UserProfileResponse buildResponseWhenNoProfile(
		User user,
		List<Project> projects,
		int projectCounts
	) {
		List<Long> projectIds = projects.stream().map(Project::getId).toList();
		Map<Long, List<String>> skillNamesByProjectId = requiredSkillUseCase.queryNamesByProjectIds(projectIds);
		Map<Long, List<ProjectRole>> rolesByProjectId = projectQueryUseCase.queryUserRolesByProjects(user.getId(), projectIds);

		return new UserProfileResponse(
			user.getId(),
			user.getEmail(),
			user.getName(),
			user.getNickname(),
			user.getAge(),
			user.getTel(),
			null,
			null,
			null,
			projectCounts,
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			buildProjectInfos(projects, skillNamesByProjectId, rolesByProjectId)
		);
	}

	@Override
	public PageResponse<List<UserProfileListResponse>> getUserProfileList(Long viewerUserId, String keyword, Pageable pageable) {
		Page<Profile> page = profileRepository.searchProfilesBySkillName(List.of(keyword), pageable);
		List<Profile> profiles = page.getContent();
		if (profiles.isEmpty()) {
			return toPageResponse(List.of(), page);
		}

		List<Long> profileIds = collectProfileIds(profiles);
		List<Long> userIds = collectDistinctUserIds(profiles);

		Map<Long, String> userIdToName = userQueryUseCase.findNamesByUserIds(userIds);
		Map<Long, List<ProfileSkill>> skillsByProfileId = loadProfileSkillsByProfileId(profileIds);
		Map<Long, Skill> skillMap = loadSkillMap(skillsByProfileId);
		Map<Long, Boolean> likedByProfileId = profileLikeQueryUseCase.isLikedByProfileIds(viewerUserId, profileIds);

		List<UserProfileListResponse> contents = profiles.stream()
			.map(profile -> toUserProfileListResponse(profile, userIdToName, skillsByProfileId, skillMap, likedByProfileId))
			.toList();

		return toPageResponse(contents, page);
	}

	@Override
	public PageResponse<List<UserProfileListResponse>> getLikedUserProfileList(Long viewerUserId, String keyword, Pageable pageable) {
		List<Long> likedProfileIds = profileLikeQueryUseCase.findLikedProfileIds(viewerUserId);
		if (likedProfileIds == null || likedProfileIds.isEmpty()) {
			return PageResponse.of(List.of(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0);
		}

		Page<Profile> page = profileRepository.searchProfilesBySkillNameInProfileIds(List.of(keyword), likedProfileIds, pageable);
		List<Profile> profiles = page.getContent();
		if (profiles.isEmpty()) {
			return toPageResponse(List.of(), page);
		}

		List<Long> profileIds = collectProfileIds(profiles);
		List<Long> userIds = collectDistinctUserIds(profiles);

		Map<Long, String> userIdToName = userQueryUseCase.findNamesByUserIds(userIds);
		Map<Long, List<ProfileSkill>> skillsByProfileId = loadProfileSkillsByProfileId(profileIds);
		Map<Long, Skill> skillMap = loadSkillMap(skillsByProfileId);
		Map<Long, Boolean> likedByProfileId = profileLikeQueryUseCase.isLikedByProfileIds(viewerUserId, profileIds);

		List<UserProfileListResponse> contents = profiles.stream()
			.map(profile -> toUserProfileListResponse(profile, userIdToName, skillsByProfileId, skillMap, likedByProfileId))
			.toList();

		return toPageResponse(contents, page);
	}

	private PageResponse<List<UserProfileListResponse>> toPageResponse(List<UserProfileListResponse> contents, Page<?> page) {
		return PageResponse.of(
			contents,
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages()
		);
	}

	private List<Long> collectProfileIds(List<Profile> profiles) {
		return profiles.stream()
			.map(Profile::getId)
			.toList();
	}

	private List<Long> collectDistinctUserIds(List<Profile> profiles) {
		return profiles.stream()
			.map(Profile::getUserId)
			.distinct()
			.toList();
	}

	private Map<Long, List<ProfileSkill>> loadProfileSkillsByProfileId(List<Long> profileIds) {
		List<ProfileSkill> profileSkills = profileRepository.getProfileSkillsByProfileIds(profileIds);
		return profileSkills.stream()
			.collect(Collectors.groupingBy(ProfileSkill::getProfileId));
	}

	private Map<Long, Skill> loadSkillMap(Map<Long, List<ProfileSkill>> skillsByProfileId) {
		List<Long> skillIds = skillsByProfileId.values()
			.stream()
			.flatMap(List::stream)
			.map(ProfileSkill::getSkillId)
			.distinct()
			.toList();

		if (skillIds.isEmpty()) {
			return Map.of();
		}

		return skillRepository.findByIdIn(skillIds).stream()
			.collect(Collectors.toMap(Skill::getId, Function.identity(), (a, b) -> a));
	}

	private UserProfileListResponse toUserProfileListResponse(
		Profile profile,
		Map<Long, String> userIdToName,
		Map<Long, List<ProfileSkill>> skillsByProfileId,
		Map<Long, Skill> skillMap,
		Map<Long, Boolean> likedByProfileId
	) {
		Long profileId = profile.getId();
		Long userId = profile.getUserId();

		List<UserProfileListResponse.SkillInfo> skillInfos = skillsByProfileId
			.getOrDefault(profileId, List.of())
			.stream()
			.map(ps -> toUserProfileListSkillInfo(ps, skillMap))
			.filter(Objects::nonNull)
			.toList();

		return new UserProfileListResponse(
			userId,
			userIdToName.get(userId),
			profile.getSelfIntroduction(),
			skillInfos,
			likedByProfileId.getOrDefault(profileId, false)
		);
	}

	private UserProfileListResponse.SkillInfo toUserProfileListSkillInfo(
		ProfileSkill profileSkill,
		Map<Long, Skill> skillMap
	) {
		Skill skill = skillMap.get(profileSkill.getSkillId());
		if (skill == null) {
			return null;
		}
		return new UserProfileListResponse.SkillInfo(
			skill.getId(),
			skill.getName(),
			profileSkill.getProficiency()
		);
	}



	private int countCompletedProjects(List<Project> projects) {
		return (int) projects.stream()
			.filter(p -> p.getStatus() == ProjectStatus.FINISHED)
			.count();
	}

	private List<UserProfileResponse.ProjectInfo> buildProjectInfos(
		List<Project> projects,
		Map<Long, List<String>> skillNamesByProjectId,
		Map<Long, List<ProjectRole>> rolesByProjectId
	) {
		return projects.stream()
			.map(project -> new UserProfileResponse.ProjectInfo(
				project.getId(),
				project.getTitle(),
				project.getDescription(),
				project.getStartDt(),
				project.getEndDt(),
				project.getMeetingType(),
				project.getStatus(),
				skillNamesByProjectId.getOrDefault(project.getId(), List.of()),
				rolesByProjectId.getOrDefault(project.getId(), List.of())
			))
			.toList();
	}

	private List<UserProfileResponse.RoleInfo> buildRoleInfos(Long profileId) {
		List<ProfileRole> profileRoles = profileRepository.getProfileRoles(profileId);
		if (profileRoles.isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> profileRoleIds = profileRoles.stream()
			.map(ProfileRole::getRoleId)
			.collect(Collectors.toList());
		Map<Long, Role> roleMap = roleRepository.findByIdIn(profileRoleIds).stream()
			.collect(Collectors.toMap(Role::getId, Function.identity()));

		return profileRoles.stream()
			.map(profileRole -> {
				Role role = roleMap.get(profileRole.getRoleId());
				if (role == null) return null;
				return new UserProfileResponse.RoleInfo(role.getId(), role.getName());
			})
			.filter(roleInfo -> roleInfo != null)
			.collect(Collectors.toList());
	}

	private List<UserProfileResponse.SchoolInfo> buildSchoolInfos(Long profileId) {
		List<ProfileSchool> profileSchools = profileRepository.getProfileSchools(profileId);
		if (profileSchools.isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> schoolIds = profileSchools.stream()
			.map(ProfileSchool::getSchoolId)
			.collect(Collectors.toList());
		Map<Long, School> schoolMap = schoolQueryUseCase.findByIdIn(schoolIds).stream()
			.collect(Collectors.toMap(School::getId, Function.identity()));

		return profileSchools.stream()
			.map(profileSchool -> {
				School school = schoolMap.get(profileSchool.getSchoolId());
				if (school == null) return null;
				return new UserProfileResponse.SchoolInfo(
					school.getId(),
					school.getName(),
					school.getAddress(),
					profileSchool.getState(),
					profileSchool.getMajor(),
					profileSchool.getStartDate(),
					profileSchool.getEndDate()
				);
			})
			.filter(schoolInfo -> schoolInfo != null)
			.collect(Collectors.toList());
	}

	private List<UserProfileResponse.SkillInfo> buildSkillInfos(Long profileId) {
		List<ProfileSkill> profileSkills = profileRepository.getProfileSkills(profileId);
		if (profileSkills.isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> skillIds = profileSkills.stream()
			.map(ProfileSkill::getSkillId)
			.collect(Collectors.toList());
		Map<Long, Skill> skillMap = skillRepository.findByIdIn(skillIds).stream()
			.collect(Collectors.toMap(Skill::getId, Function.identity()));

		return profileSkills.stream()
			.map(profileSkill -> {
				Skill skill = skillMap.get(profileSkill.getSkillId());
				if (skill == null) return null;
				return new UserProfileResponse.SkillInfo(skill.getId(), skill.getName(),profileSkill.getProficiency());
			})
			.filter(skillInfo -> skillInfo != null)
			.collect(Collectors.toList());
	}

	private List<UserProfileResponse.PortfolioInfo> buildPortfolioInfos(Long profileId) {
		List<ProjectPortfolio> projectPortfolios = profileRepository.getProjectPortfolios(profileId);
		if (projectPortfolios.isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> portfolioIds = projectPortfolios.stream()
			.map(ProjectPortfolio::getPortfolioId)
			.collect(Collectors.toList());
		Map<Long, Portfolio> portfolioMap = portfolioRepository.findByIdIn(portfolioIds).stream()
			.collect(Collectors.toMap(Portfolio::getId, Function.identity()));

		return projectPortfolios.stream()
			.map(projectPortfolio -> {
				Portfolio portfolio = portfolioMap.get(projectPortfolio.getPortfolioId());
				if (portfolio == null) return null;
				return new UserProfileResponse.PortfolioInfo(
					portfolio.getId(),
					portfolio.getType(),
					portfolio.getStartDate(),
					portfolio.getEndDate(),
					portfolio.getContent(),
					portfolio.getOrganizationName()
				);
			})
			.filter(portfolioInfo -> portfolioInfo != null)
			.collect(Collectors.toList());
	}

	private String buildResidenceText(Long subRegionId) {
		if (subRegionId == null) {
			return null;
		}
		return regionQueryUseCase.getRegion(subRegionId);
	}


}
