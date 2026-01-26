package com.sidework.profile.application.service;

import java.util.List;
import java.util.Map;
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
import com.sidework.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProfileCommandService implements ProfileCommandUseCase {

	private final ProfileOutPort profileRepository;
	private final PortfolioOutPort portfolioRepository;

	private final UserQueryUseCase userRepository;
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
					ProfileSchool.create(profile.getId(),school.state(),school.major(),school.startDate(),school.endDate()))
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
		if (command.portfolios()!=null)
		{
			profileRepository.deleteAllProjectPortfoliosByProfileId(profile.getId());
			if(!command.portfolios().isEmpty())
			{
				List<Long> portfolioIds = command.portfolios().stream()
					.map(ProfileUpdateCommand.PortfolioUpdateRequest::portfolioId)
					.filter(id -> id != null)
					.toList();

				//기존 존재 포폴
				Map<Long, Portfolio> existingPortfolioMap =
					portfolioIds.isEmpty()
						? Map.of()
						: portfolioRepository.findByIdIn(portfolioIds).stream()
						.collect(Collectors.toMap(Portfolio::getId, p -> p));

				List<Portfolio> portfoliosToSave = command.portfolios().stream()
					.map(req -> {
						if (req.portfolioId() != null &&
							existingPortfolioMap.containsKey(req.portfolioId())) {
							//기존 존재 포폴은 업뎃
							return Portfolio.builder()
								.id(req.portfolioId())
								.type(req.type())
								.startDate(req.startDate())
								.endDate(req.endDate())
								.content(req.content())
								.build();
						}
						//새로 생긴 포폴은 신규 생성
						return Portfolio.create(
							req.type(),
							req.startDate(),
							req.endDate(),
							req.content()
						);
					})
					.toList();

				portfolioRepository.savePortfolios(portfoliosToSave);

				List<ProjectPortfolio> projectPortfolios = portfoliosToSave.stream()
					.map(p -> ProjectPortfolio.create(profile.getId(), p.getId()))
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
