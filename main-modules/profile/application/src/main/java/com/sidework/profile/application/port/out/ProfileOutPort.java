package com.sidework.profile.application.port.out;

import com.sidework.profile.application.port.in.ProfileUpdateCommand;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.domain.ProjectPortfolio;

import java.util.List;

public interface ProfileOutPort
{
	boolean existsByUserId(Long userId);
	Profile getProfileByUserId(Long userId);
	List<ProfileRole> getProfileRoles(Long profileId);
	List<ProfileSchool> getProfileSchools(Long profileId);
	List<ProfileSkill> getProfileSkills(Long profileId);
	List<ProjectPortfolio> getProjectPortfolios(Long profileId);
	void saveProfileRoles(List<ProfileRole> profileRoles);
	void saveProfileSchools(List<ProfileSchool> profileSchools);
	void saveProfileSkills(List<ProfileSkill> profileSkills);
	void saveProjectPortfolios(List<ProjectPortfolio> projectPortfolios);
	void deleteAllProfileRolesByProfileId(Long profileId);
	void deleteAllProfileSchoolsByProfileId(Long profileId);
	void deleteAllProfileSkillsByProfileId(Long profileId);
	void deleteAllProjectPortfoliosByProfileId(Long profileId);
	boolean existsProjectPortfolioByPortfolioIdAndProfileIdNot(Long portfolioId, Long profileId);
	boolean existsByIdAndUserId(Long profileId,Long userId);

}
