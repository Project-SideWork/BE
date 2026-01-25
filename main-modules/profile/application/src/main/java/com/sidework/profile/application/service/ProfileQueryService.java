package com.sidework.profile.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.profile.application.adapter.UserProfileResponse;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;
import com.sidework.profile.application.port.out.PortfolioOutPort;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.application.port.out.RoleOutPort;
import com.sidework.profile.application.port.out.SchoolOutPort;
import com.sidework.profile.application.port.out.SkillOutPort;
import com.sidework.profile.domain.Portfolio;
import com.sidework.profile.domain.Role;
import com.sidework.profile.domain.School;
import com.sidework.profile.domain.Skill;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileQueryService implements ProfileQueryUseCase
{
	private final ProfileOutPort profileRepository;
	private final SchoolOutPort schoolRepository;
	private final SkillOutPort skillRepository;
	private final PortfolioOutPort portfolioRepository;
	private final RoleOutPort roleRepository;

	private final UserQueryUseCase userQueryUseCase;
	private final ProjectQueryUseCase projectQueryUseCase;

	@Override
	public UserProfileResponse getProfileByUserId(Long userId) {
		Profile profile = profileRepository.getProfileByUserId(userId);
		if (profile == null) {
			User user = userQueryUseCase.findById(userId);
			List<Project> projects = projectQueryUseCase.queryByUserId(userId);
			List<UserProfileResponse.ProjectInfo> projectInfos = projects.stream()
				.map(project -> new UserProfileResponse.ProjectInfo(
					project.getId(),
					project.getTitle(),
					project.getDescription(),
					project.getStartDt(),
					project.getEndDt(),
					project.getMeetingType(),
					project.getStatus()
				))
				.collect(Collectors.toList());

			return new UserProfileResponse(
					user.getId(),
					user.getEmail(),
					user.getName(),
					user.getNickname(),
					user.getAge(),
					user.getTel(),
					null,
					new ArrayList<>(),
					new ArrayList<>(),
					new ArrayList<>(),
					new ArrayList<>(),
					projectInfos
			);
		}

		User user = userQueryUseCase.findById(userId);

		List<ProfileRole> profileRoles = profileRepository.getProfileRoles(profile.getId());

		List<UserProfileResponse.RoleInfo> roles = new ArrayList<>();
		if(!profileRoles.isEmpty())
		{
			List<Long> profileRoleIds = profileRoles.stream()
				.map(ProfileRole::getRoleId)
				.collect(Collectors.toList());
			Map<Long,Role> roleMap = roleRepository.findByIdIn(profileRoleIds).stream()
				.collect(Collectors.toMap(Role::getId, Function.identity()));

			roles = profileRoles.stream()
				.map(profileRole -> {
					Role role = roleMap.get(profileRole.getRoleId());
					if(role == null) return null;
					return new UserProfileResponse.RoleInfo(
						role.getId(),
						role.getName()
					);
				})
				.filter(roleInfo -> roleInfo != null)
				.collect(Collectors.toList());
		}

		List<ProfileSchool> profileSchools = profileRepository.getProfileSchools(profile.getId());

		List<UserProfileResponse.SchoolInfo> schools = new ArrayList<>();
		if (!profileSchools.isEmpty()) {
			List<Long> schoolIds = profileSchools.stream()
				.map(ProfileSchool::getSchoolId)
				.collect(Collectors.toList());
			Map<Long, School> schoolMap = schoolRepository.findByIdIn(schoolIds).stream()
				.collect(Collectors.toMap(School::getId, Function.identity()));
			
			schools = profileSchools.stream()
				.map(profileSchool -> {
					School school = schoolMap.get(profileSchool.getSchoolId());
					if (school == null) {
						return null;
					}
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

		List<ProfileSkill> profileSkills = profileRepository.getProfileSkills(profile.getId());

		List<UserProfileResponse.SkillInfo> skills = new ArrayList<>();
		if (!profileSkills.isEmpty()) {
			List<Long> skillIds = profileSkills.stream()
				.map(ProfileSkill::getSkillId)
				.collect(Collectors.toList());
			Map<Long, Skill> skillMap = skillRepository.findByIdIn(skillIds).stream()
				.collect(Collectors.toMap(Skill::getId, Function.identity()));

			skills = profileSkills.stream()
				.map(profileSkill -> {
					Skill skill = skillMap.get(profileSkill.getSkillId());
					if (skill == null) {
						return null;
					}
					return new UserProfileResponse.SkillInfo(
						skill.getId(),
						skill.getName()
					);
				})
				.filter(skillInfo -> skillInfo != null)
				.collect(Collectors.toList());
		}


		List<ProjectPortfolio> projectPortfolios = profileRepository.getProjectPortfolios(profile.getId());

		List<UserProfileResponse.PortfolioInfo> portfolios = new ArrayList<>();
		if (!projectPortfolios.isEmpty()) {

			List<Long> portfolioIds = projectPortfolios.stream()
				.map(ProjectPortfolio::getPortfolioId)
				.collect(Collectors.toList());
			Map<Long, Portfolio> portfolioMap = portfolioRepository.findByIdIn(portfolioIds).stream()
				.collect(Collectors.toMap(Portfolio::getId, Function.identity()));

			portfolios = projectPortfolios.stream()
				.map(projectPortfolio -> {
					Portfolio portfolio = portfolioMap.get(projectPortfolio.getPortfolioId());
					if (portfolio == null) {
						return null;
					}
					return new UserProfileResponse.PortfolioInfo(
						portfolio.getId(),
						portfolio.getType(),
						portfolio.getStartDate(),
						portfolio.getEndDate(),
						portfolio.getContent()
					);
				})
				.filter(portfolioInfo -> portfolioInfo != null)
				.collect(Collectors.toList());
		}

		List<Project> projects = projectQueryUseCase.queryByUserId(userId);
		List<UserProfileResponse.ProjectInfo> projectInfos = projects.stream()
			.map(project -> new UserProfileResponse.ProjectInfo(
				project.getId(),
				project.getTitle(),
				project.getDescription(),
				project.getStartDt(),
				project.getEndDt(),
				project.getMeetingType(),
				project.getStatus()
			))
			.collect(Collectors.toList());

		return new UserProfileResponse(
				user.getId(),
				user.getEmail(),
				user.getName(),
				user.getNickname(),
				user.getAge(),
				user.getTel(),
				profile.getId(),
				roles,
				schools,
				skills,
				portfolios,
				projectInfos
		);
	}

}
