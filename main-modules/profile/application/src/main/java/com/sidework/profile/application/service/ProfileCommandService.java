package com.sidework.profile.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.profile.application.exception.ProfileNotFoundException;
import com.sidework.profile.application.port.in.ProfileCommandUseCase;
import com.sidework.profile.application.port.in.ProfileUpdateCommand;
import com.sidework.profile.application.port.out.PortfolioOutPort;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.domain.Portfolio;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.domain.ProjectPortfolio;
import com.sidework.user.application.port.in.UserQueryUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
@Slf4j
public class ProfileCommandService implements ProfileCommandUseCase {

	private final ProfileOutPort profileRepository;
	private final PortfolioOutPort portfolioRepository;

	@Override
	public void update(Long userId, ProfileUpdateCommand command)
	{
		Profile profile=profileRepository.getProfileByUserId(userId);
		if(profile==null)
		{
			throw new ProfileNotFoundException(userId);
		}

		if(command.schools()!=null)
		{
			profileRepository.deleteAllProfileSchoolsByProfileId(profile.getId());
			List<ProfileSchool> schools = command.schools().stream()
				.map(school ->
					ProfileSchool.create(profile.getId(),school.schoolId(),school.state(),school.major(),school.startDate(),school.endDate()))
				.toList();
			profileRepository.saveProfileSchools(schools);
		}
		if (command.skills()!=null)
		{
			profileRepository.deleteAllProfileSkillsByProfileId(profile.getId());
			List<ProfileSkill> skills = command.skills().stream()
				.map(profileSkill -> ProfileSkill.create(profile.getId(),profileSkill))
				.toList();
			profileRepository.saveProfileSkills(skills);
		}

		//TODO: 포폴 <-> 프로필 연결 확인
		if (command.portfolios() != null) {
			Long profileId= profile.getId();

			List<ProjectPortfolio> existedPortfolios = profileRepository.getProjectPortfolios(profileId);

			profileRepository.deleteAllProjectPortfoliosByProfileId(profile.getId());

			if (!existedPortfolios.isEmpty()) {
				portfolioRepository.deletePortfolios(
					existedPortfolios.stream()
						.map(ProjectPortfolio::getPortfolioId)
						.toList()
				);
			}

			if (!command.portfolios().isEmpty())
			{
				List<Long> savedPortfolios= portfolioRepository.savePortfolios(
					command.portfolios().stream()
						.map(portfolio ->
							Portfolio.create(
								portfolio.type(),
								portfolio.startDate(),
								portfolio.endDate(),
								portfolio.content()
							)
						)
						.toList()
				);

				List<ProjectPortfolio> projectPortfolios =
					savedPortfolios.stream()
						.map(p -> ProjectPortfolio.create(profile.getId(),p))
						.toList();

				profileRepository.saveProjectPortfolios(projectPortfolios);
			}
		}


		if(command.roleIds()!=null)
		{
			profileRepository.deleteAllProfileRolesByProfileId(profile.getId());
			List<ProfileRole> roles = command.roleIds().stream()
				.map(role -> ProfileRole.create(profile.getId(),role))
				.toList();
			profileRepository.saveProfileRoles(roles);
		}




	}
}
