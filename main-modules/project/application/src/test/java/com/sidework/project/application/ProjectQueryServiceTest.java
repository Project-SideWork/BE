package com.sidework.project.application;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.MyProjectSummaryResponse;
import com.sidework.project.application.adapter.ProjectDetailResponse;
import com.sidework.project.application.adapter.ProjectListResponse;
import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.application.exception.ProjectHasNoMembersException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.in.ProjectLikeQueryUseCase;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectRecruitPositionOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewStatOutPort;
import com.sidework.project.application.service.ProjectQueryService;
import com.sidework.project.domain.ProjectUserReviewStat;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRecruitPosition;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;
import com.sidework.project.domain.SkillLevel;
import com.sidework.skill.application.port.in.ProjectPreferredSkillQueryUseCase;
import com.sidework.skill.application.port.in.ProjectRequiredQueryUseCase;
import com.sidework.user.application.port.in.UserQueryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectQueryServiceTest {

    @Mock
    private ProjectOutPort projectRepository;

    @Mock
    private ProjectUserOutPort projectUserRepository;

    @Mock
    private ProjectRecruitPositionOutPort projectRecruitPositionRepository;

    @Mock
    private ProjectPreferredSkillQueryUseCase projectPreferredSkillQueryUseCase;

    @Mock
    private ProjectRequiredQueryUseCase projectRequiredQueryUseCase;

    @Mock
    private UserQueryUseCase userQueryUseCase;

    @Mock
    private ProjectLikeQueryUseCase projectLikeQueryUseCase;

    @Mock
    private ProjectUserReviewStatOutPort projectUserReviewStatRepository;

    @Mock
    private ProjectUserReviewOutPort projectUserReviewOutPort;

    @InjectMocks
    private ProjectQueryService queryService;

    @Test
    void queryById_프로젝트가_있으면_반환한다() {
        Long projectId = 1L;
        Project project = createProject(projectId);

        when(projectRepository.findById(projectId)).thenReturn(project);

        Project result = queryService.queryById(projectId);

        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals("테스트 프로젝트", result.getTitle());
        verify(projectRepository).findById(projectId);
    }

    @Test
    void queryByUserId_참여_프로젝트가_있으면_목록을_반환한다() {
        Long userId = 1L;
        List<Long> projectIds = List.of(1L, 2L);
        Project project1 = createProject(1L);
        Project project2 = createProject(2L);

        when(projectUserRepository.queryAllProjectIds(userId)).thenReturn(projectIds);
        when(projectRepository.findByIdIn(projectIds)).thenReturn(List.of(project1, project2));

        List<Project> result = queryService.queryByUserId(userId);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void queryByUserId_참여_프로젝트가_없으면_빈_목록을_반환한다() {
        Long userId = 1L;
        when(projectUserRepository.queryAllProjectIds(userId)).thenReturn(List.of());

        List<Project> result = queryService.queryByUserId(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void queryProjectDetail_정상_조회_시_ProjectDetailResponse를_반환한다() {
        Long projectId = 1L;
        Project project = createProject(projectId);
        List<ProjectUser> members = List.of(
            ProjectUser.builder().userId(1L).profileId(10L).role(ProjectRole.OWNER).status(ApplyStatus.ACCEPTED).build(),
            ProjectUser.builder().userId(2L).profileId(20L).role(ProjectRole.BACKEND).status(ApplyStatus.ACCEPTED).build()
        );
        List<ProjectRecruitPosition> positions = List.of(
            ProjectRecruitPosition.builder().role(ProjectRole.BACKEND).headCount(1).currentCount(0).level(SkillLevel.JUNIOR).build()
        );
        List<String> requiredStacks = List.of("Java", "Spring");
        List<String> preferredStacks = List.of("Redis");

        when(projectRepository.findById(projectId)).thenReturn(project);
        when(projectUserRepository.findAllByProjectId(projectId)).thenReturn(members);
        when(projectRecruitPositionRepository.getProjectRecruitPositions(projectId)).thenReturn(positions);
        when(projectRequiredQueryUseCase.queryNamesByProjectId(projectId)).thenReturn(requiredStacks);
        when(projectPreferredSkillQueryUseCase.queryNamesByProjectId(projectId)).thenReturn(preferredStacks);
        when(projectUserReviewStatRepository.getAllReviewStatsByUserIds(anyList()))
            .thenReturn(List.of(
                ProjectUserReviewStat.builder().userId(1L).ratingScore(18.0).ratingCount(4L).build(),
                ProjectUserReviewStat.builder().userId(2L).ratingScore(8.0).ratingCount(2L).build()
            ));

        ProjectDetailResponse result = queryService.queryProjectDetail(projectId);

        assertNotNull(result);
        assertEquals(projectId, result.id());
        assertEquals(project.getTitle(), result.title());
        assertEquals(2, result.teamMembers().size());
        List<ProjectDetailResponse.ProjectMemberResponse> membersSorted = result.teamMembers().stream()
            .sorted(Comparator.comparing(ProjectDetailResponse.ProjectMemberResponse::userId))
            .toList();
        assertEquals(4.5, membersSorted.get(0).score());
        assertEquals(4.0, membersSorted.get(1).score());
        assertEquals(1, result.recruitPositions().size());
        assertEquals(ProjectRole.BACKEND, result.recruitPositions().get(0).role());
        assertEquals(1, result.recruitPositions().get(0).headCount());
        assertEquals(0, result.recruitPositions().get(0).currentCount());
        assertEquals(SkillLevel.JUNIOR, result.recruitPositions().get(0).level());
        assertEquals(requiredStacks, result.requiredStacks());
        assertEquals(preferredStacks, result.preferredStacks());
    }

    @Test
    void queryProjectDetail_프로젝트가_없으면_ProjectNotFoundException을_던진다() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(null);

        assertThrows(ProjectNotFoundException.class, () -> queryService.queryProjectDetail(projectId));
    }

    @Test
    void queryProjectDetail_멤버가_없으면_ProjectHasNoMembersException을_던진다() {
        Long projectId = 1L;
        Project project = createProject(projectId);
        when(projectRepository.findById(projectId)).thenReturn(project);
        when(projectUserRepository.findAllByProjectId(projectId)).thenReturn(List.of());

        assertThrows(ProjectHasNoMembersException.class, () -> queryService.queryProjectDetail(projectId));
    }

    @Test
    void queryProjectRecruitPosition_포지션이_있으면_목록을_반환한다() {
        Long projectId = 1L;
        List<ProjectRecruitPosition> positions = List.of(
            ProjectRecruitPosition.builder().projectId(projectId).role(ProjectRole.BACKEND).headCount(1).level(SkillLevel.JUNIOR).build()
        );
        when(projectRecruitPositionRepository.getProjectRecruitPositions(projectId)).thenReturn(positions);

        List<ProjectRecruitPosition> result = queryService.queryProjectRecruitPosition(projectId);

        assertEquals(1, result.size());
        assertEquals(ProjectRole.BACKEND, result.get(0).getRole());
    }

    @Test
    void queryProjectList_프로젝트가_없으면_빈_페이지_반환한다() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(projectRepository.findPage(pageable)).thenReturn(emptyPage);

        PageResponse<List<ProjectListResponse>> result = queryService.queryProjectList(userId, pageable);

        assertNotNull(result.content());
        assertTrue(result.content().isEmpty());
        assertEquals(1, result.page());
        assertEquals(20, result.size());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        verify(projectRepository).findPage(pageable);
    }

    @Test
    void queryProjectList_프로젝트가_있으면_배치_조회_후_PageResponse_반환한다() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Project project1 = createProject(1L);
        Project project2 = createProject(2L);
        Page<Project> page = new PageImpl<>(List.of(project1, project2), pageable, 2);

        List<ProjectRecruitPosition> positions1 = List.of(
            ProjectRecruitPosition.builder().projectId(1L).role(ProjectRole.BACKEND).headCount(1).currentCount(0).level(SkillLevel.JUNIOR).build()
        );
        when(projectRepository.findPage(pageable)).thenReturn(page);
        when(projectRepository.getProjectRecruitPositionsByProjectIds(List.of(1L, 2L)))
            .thenReturn(Map.of(1L, positions1, 2L, List.of()));
        when(projectRequiredQueryUseCase.queryNamesByProjectIds(List.of(1L, 2L)))
            .thenReturn(Map.of(1L, List.of("Java", "Spring"), 2L, List.of("React")));
        when(projectUserRepository.findOwnerUserIdByProjectIds(List.of(1L, 2L)))
            .thenReturn(Map.of(1L, 10L, 2L, 10L));
        when(userQueryUseCase.findNamesByUserIds(List.of(10L)))
            .thenReturn(Map.of(10L, "테스트유저"));
        when(projectLikeQueryUseCase.isLikedByProjectIds(userId, List.of(1L, 2L)))
            .thenReturn(Map.of(1L, true, 2L, false));

        PageResponse<List<ProjectListResponse>> result = queryService.queryProjectList(userId, pageable);

        assertNotNull(result.content());
        assertEquals(2, result.content().size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());

        ProjectListResponse first = result.content().get(0);
        assertEquals(1L, first.projectId());
        assertEquals("테스트 프로젝트", first.title());
        assertEquals(List.of("Java", "Spring"), first.requiredStacks());
        assertEquals("테스트유저", first.creatorName());
        assertTrue(first.liked());
        assertEquals(1, first.recruitPositions().size());
        assertEquals(ProjectRole.BACKEND, first.recruitPositions().get(0).role());

        ProjectListResponse second = result.content().get(1);
        assertEquals(2L, second.projectId());
        assertFalse(second.liked());
    }

    @Test
    void queryProjectList_키워드와_스킬이_있을때_ProjectOutPort_search를_호출한다() {
        Long userId = 1L;
        String keyword = "테스트";
        List<Long> skillIds = List.of(1L, 2L);
        Pageable pageable = PageRequest.of(0, 10);

        Project project1 = createProject(1L);
        Page<Project> page = new PageImpl<>(List.of(project1), pageable, 1);

        when(projectRepository.search(keyword, skillIds, pageable)).thenReturn(page);
        when(projectRepository.getProjectRecruitPositionsByProjectIds(List.of(1L)))
            .thenReturn(Map.of());
        when(projectRequiredQueryUseCase.queryNamesByProjectIds(List.of(1L)))
            .thenReturn(Map.of(1L, List.of("Java")));
        when(projectUserRepository.findOwnerUserIdByProjectIds(List.of(1L)))
            .thenReturn(Map.of(1L, 10L));
        when(userQueryUseCase.findNamesByUserIds(List.of(10L)))
            .thenReturn(Map.of(10L, "테스트유저"));
        when(projectLikeQueryUseCase.isLikedByProjectIds(userId, List.of(1L)))
            .thenReturn(Map.of(1L, true));

        PageResponse<List<ProjectListResponse>> result =
            queryService.queryProjectList(userId, keyword, skillIds, pageable);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        verify(projectRepository).search(keyword, skillIds, pageable);
    }

    @Test
    void queryLikedProjectList_프로젝트가_있으면_배치_조회_후_PageResponse_반환한다() {
        Long userId = 1L;
        String keyword = "테스트";
        List<Long> skillIds = List.of(1L, 2L);
        Pageable pageable = PageRequest.of(0, 20);

        Project project1 = createProject(1L);
        Project project2 = createProject(2L);
        Page<Project> page = new PageImpl<>(List.of(project1, project2), pageable, 2);

        List<ProjectRecruitPosition> positions1 = List.of(
            ProjectRecruitPosition.builder()
                .projectId(1L)
                .role(ProjectRole.BACKEND)
                .headCount(1)
                .currentCount(0)
                .level(SkillLevel.JUNIOR)
                .build()
        );

        when(projectRepository.searchLiked(keyword, skillIds, userId, pageable)).thenReturn(page);
        when(projectRepository.getProjectRecruitPositionsByProjectIds(List.of(1L, 2L)))
            .thenReturn(Map.of(1L, positions1, 2L, List.of()));
        when(projectRequiredQueryUseCase.queryNamesByProjectIds(List.of(1L, 2L)))
            .thenReturn(Map.of(1L, List.of("Java", "Spring"), 2L, List.of("React")));
        when(projectUserRepository.findOwnerUserIdByProjectIds(List.of(1L, 2L)))
            .thenReturn(Map.of(1L, 10L, 2L, 10L));
        when(userQueryUseCase.findNamesByUserIds(List.of(10L)))
            .thenReturn(Map.of(10L, "테스트유저"));
        when(projectLikeQueryUseCase.isLikedByProjectIds(userId, List.of(1L, 2L)))
            .thenReturn(Map.of(1L, true, 2L, false));

        PageResponse<List<ProjectListResponse>> result =
            queryService.queryLikedProjectList(userId, keyword, skillIds, pageable);

        assertNotNull(result.content());
        assertEquals(2, result.content().size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());

        ProjectListResponse first = result.content().get(0);
        assertEquals(1L, first.projectId());
        assertEquals("테스트 프로젝트", first.title());
        assertEquals(List.of("Java", "Spring"), first.requiredStacks());
        assertEquals("테스트유저", first.creatorName());
        assertTrue(first.liked());

        ProjectListResponse second = result.content().get(1);
        assertEquals(2L, second.projectId());
        assertFalse(second.liked());
    }

    @Test
    void queryLikedProjectList_프로젝트가_없으면_빈_페이지_반환한다() {
        Long userId = 1L;
        String keyword = "테스트";
        List<Long> skillIds = List.of(1L, 2L);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(projectRepository.searchLiked(keyword, skillIds, userId, pageable)).thenReturn(emptyPage);

        PageResponse<List<ProjectListResponse>> result =
            queryService.queryLikedProjectList(userId, keyword, skillIds, pageable);

        assertNotNull(result.content());
        assertTrue(result.content().isEmpty());
        assertEquals(1, result.page());
        assertEquals(20, result.size());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
    }

    @Test
    void queryAverageReviewScoresByUserIds_스탯이_있으면_userId별_평균을_반환한다() {
        when(projectUserReviewStatRepository.getAllReviewStatsByUserIds(List.of(1L, 2L)))
            .thenReturn(List.of(
                ProjectUserReviewStat.builder().userId(1L).ratingScore(9.0).ratingCount(3L).build(),
                ProjectUserReviewStat.builder().userId(2L).ratingScore(5.0).ratingCount(1L).build()
            ));

        Map<Long, Double> result = queryService.queryAverageReviewScoresByUserIds(List.of(1L, 2L));

        assertEquals(2, result.size());
        assertEquals(3.0, result.get(1L));
        assertEquals(5.0, result.get(2L));
    }

    @Test
    void queryAverageReviewScoresByUserIds_userIds가_비어있으면_빈_맵을_반환한다() {
        assertTrue(queryService.queryAverageReviewScoresByUserIds(List.of()).isEmpty());
    }

    @Test
    void queryMyProjectSummary_참여한_프로젝트가_있으면_id와_title로_응답을_만든다() {
        Long userId = 1L;
        when(projectUserRepository.getMyProjectSummary(userId)).thenReturn(
            List.of(
                new ProjectTitleDto(10L, "프로젝트A"),
                new ProjectTitleDto(20L, "프로젝트B")
            )
        );

        List<MyProjectSummaryResponse> result = queryService.queryMyProjectSummary(userId);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).projectId());
        assertEquals("프로젝트A", result.get(0).title());
        assertEquals(20L, result.get(1).projectId());
        assertEquals("프로젝트B", result.get(1).title());
        verify(projectUserRepository).getMyProjectSummary(userId);
    }

    private Project createProject(Long id) {
        return Project.builder()
            .id(id)
            .title("테스트 프로젝트")
            .description("설명")
            .startDt(LocalDate.of(2025, 1, 1))
            .endDt(LocalDate.of(2025, 3, 31))
            .meetingType(MeetingType.HYBRID)
            .status(ProjectStatus.RECRUITING)
            .build();
    }
}
