package com.sidework.project.application.port.out;


import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectUser;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProjectUserOutPort {
    void save(ProjectUser projectUser);
    List<ProjectRole> queryUserRolesByProject(Long userId, Long projectId);
    Map<Long, List<ProjectRole>> queryUserRolesByProjects(Long userId, List<Long> projectIds);
    List<Long> queryAllProjectIds(Long userId);
    List<Long> pageByUserId(Long userId, Pageable pageable);
	Optional<ProjectUser> findByProjectIdAndUserId(Long projectId, Long userId);
	Optional<ProjectUser> findByProjectIdAndUserIdAndRole(Long projectId, Long userId, ProjectRole role);
	List<ProjectUser> findAllByProjectId(Long projectId);
	Map<Long, Long> findOwnerUserIdByProjectIds(List<Long> projectIds);
	Optional<ProjectUser> findAcceptedByProjectIdAndUserId(Long projectId, Long userId);
	List<ProjectTitleDto> getMyProjectSummary(Long userId);
    Long findProjectCountByUserId(Long userId);
}
