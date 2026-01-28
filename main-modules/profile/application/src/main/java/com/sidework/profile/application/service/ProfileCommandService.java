package com.sidework.profile.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

		if (command.portfolios() != null) {
			Long profileId = profile.getId();

			if (command.portfolios().isEmpty()) {
				List<ProjectPortfolio> existedPortfolios = profileRepository.getProjectPortfolios(profileId);
				profileRepository.deleteAllProjectPortfoliosByProfileId(profileId);

				if (!existedPortfolios.isEmpty()) {
					List<Long> portfolioIdsToDelete = existedPortfolios.stream()
						.map(ProjectPortfolio::getPortfolioId)
						.filter(id -> !profileRepository.existsProjectPortfolioByPortfolioIdAndProfileIdNot(id, profileId))
						.toList();

					if (!portfolioIdsToDelete.isEmpty()) {
						portfolioRepository.deletePortfolios(portfolioIdsToDelete);
					}
				}
			} else {
				List<ProjectPortfolio> existedPortfolios = profileRepository.getProjectPortfolios(profileId);
				List<Long> existingPortfolioIds = existedPortfolios.stream()
					.map(ProjectPortfolio::getPortfolioId)
					.toList();

				List<Long> requestedPortfolioIds = command.portfolios().stream()
					.map(ProfileUpdateCommand.PortfolioUpdateRequest::portfolioId)
					.filter(Objects::nonNull)
					.toList();

				Map<Long, Portfolio> existingPortfolioMap = requestedPortfolioIds.isEmpty()
					? Map.of()
					: portfolioRepository.findByIdIn(requestedPortfolioIds).stream()
						.collect(Collectors.toMap(Portfolio::getId, p -> p));

				List<Portfolio> portfoliosToSave = new ArrayList<>();
				for (ProfileUpdateCommand.PortfolioUpdateRequest req : command.portfolios()) {
					if (req.portfolioId() != null && existingPortfolioMap.containsKey(req.portfolioId())) {
						Portfolio existing = existingPortfolioMap.get(req.portfolioId());
						Portfolio updated = Portfolio.builder()
							.id(existing.getId())
							.type(req.type())
							.startDate(req.startDate())
							.endDate(req.endDate())
							.content(req.content())
							.build();
						portfoliosToSave.add(updated);
					} else {
						portfoliosToSave.add(
							Portfolio.create(
								req.type(),
								req.startDate(),
								req.endDate(),
								req.content()
							)
						);
					}
				}

				profileRepository.deleteAllProjectPortfoliosByProfileId(profileId);

				List<Long> portfolioIdsToDelete = existingPortfolioIds.stream()
					.filter(id -> !requestedPortfolioIds.contains(id))
					.filter(id -> !profileRepository.existsProjectPortfolioByPortfolioIdAndProfileIdNot(id, profileId))
					.toList();

				if (!portfolioIdsToDelete.isEmpty()) {
					portfolioRepository.deletePortfolios(portfolioIdsToDelete);
				}

				List<Long> savedPortfolioIds = portfolioRepository.savePortfolios(portfoliosToSave);

				List<ProjectPortfolio> projectPortfolios = savedPortfolioIds.stream()
					.map(id -> ProjectPortfolio.create(profileId, id))
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
