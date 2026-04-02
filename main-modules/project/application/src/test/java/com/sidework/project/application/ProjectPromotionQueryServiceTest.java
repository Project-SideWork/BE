package com.sidework.project.application;

import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.ProjectPromotionDetailResponse;
import com.sidework.project.application.adapter.ProjectPromotionListResponse;
import com.sidework.project.application.dto.ProjectPromotionListRow;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectPromotionOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewStatOutPort;
import com.sidework.project.application.service.ProjectPromotionQueryService;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectPromotion;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;
import com.sidework.project.domain.ProjectUserReviewStat;
import com.sidework.region.application.port.in.RegionQueryUseCase;
import com.sidework.skill.application.port.in.ProjectPromotionSkillQueryUseCase;
import com.sidework.user.application.port.in.UserQueryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectPromotionQueryServiceTest {

	@Mock
	private ProjectPromotionOutPort projectPromotionOutPort;

	@Mock
	private ProjectOutPort projectOutPort;

	@Mock
	private ProjectUserOutPort projectUserOutPort;

	@Mock
	private ProjectUserReviewStatOutPort projectUserReviewStatOutPort;

	@Mock
	private ProjectPromotionSkillQueryUseCase projectPromotionSkillQueryUseCase;

	@Mock
	private RegionQueryUseCase regionQueryUseCase;

	@Mock
	private UserQueryUseCase userQueryUseCase;

	@InjectMocks
	private ProjectPromotionQueryService service;

	@Test
	void 홍보글_목록을_조회하면_PageResponse로_반환한다() {
		String keyword = "키워드";
		List<Long> skillIds = List.of(1L, 2L);
		Pageable pageable = PageRequest.of(0, 20);
		List<ProjectPromotionListRow> rows = List.of(
			new ProjectPromotionListRow(1L, "제목", "설명", 100L)
		);
		Page<ProjectPromotionListRow> page = new PageImpl<>(rows, pageable, 1L);
		when(projectPromotionOutPort.search(eq(keyword), eq(skillIds), eq(pageable))).thenReturn(page);
		when(projectPromotionSkillQueryUseCase.queryNamesByPromotionIds(List.of(100L)))
			.thenReturn(Map.of(100L, List.of("Java", "Spring")));

		PageResponse<List<ProjectPromotionListResponse>> result =
			service.queryProjectPromotionList(keyword, skillIds, pageable);

		assertEquals(1, result.content().size());
		assertEquals(100L, result.content().get(0).promotionId());
		assertEquals(1L, result.content().get(0).projectId());
		assertEquals("제목", result.content().get(0).title());
		assertEquals("설명", result.content().get(0).description());
		assertEquals(List.of("Java", "Spring"), result.content().get(0).usedStacks());
		assertEquals(1, result.page());
		assertEquals(20, result.size());
		assertEquals(1L, result.totalElements());
		assertEquals(1, result.totalPages());
		verify(projectPromotionOutPort).search(keyword, skillIds, pageable);
		verify(projectPromotionSkillQueryUseCase).queryNamesByPromotionIds(List.of(100L));
	}

	@Test
	void 홍보글_상세를_조회하면_프로젝트와_팀_정보를_담은_응답을_반환한다() {
		Long promotionId = 100L;
		Long projectId = 10L;

		ProjectPromotion promotion = ProjectPromotion.builder()
			.id(promotionId)
			.projectId(projectId)
			.userId(1L)
			.description("홍보 설명")
			.demoUrl("https://demo")
			.build();

		Project project = Project.builder()
			.id(projectId)
			.meetRegionId(7L)
			.title("제목")
			.description("p")
			.startDt(LocalDate.of(2024, 1, 1))
			.endDt(LocalDate.of(2024, 4, 1))
			.meetingType(MeetingType.HYBRID)
			.status(ProjectStatus.RECRUITING)
			.build();

		ProjectUser member = ProjectUser.builder()
			.id(1L)
			.projectId(projectId)
			.userId(5L)
			.profileId(20L)
			.status(ApplyStatus.ACCEPTED)
			.role(ProjectRole.OWNER)
			.build();

		when(projectPromotionOutPort.findById(promotionId)).thenReturn(promotion);
		when(projectOutPort.findById(projectId)).thenReturn(project);
		when(projectPromotionSkillQueryUseCase.queryNamesByPromotionId(promotionId)).thenReturn(List.of("Go"));
		when(regionQueryUseCase.getRegion(7L)).thenReturn("부산");
		when(projectUserOutPort.findAllByProjectId(projectId)).thenReturn(List.of(member));
		when(projectUserReviewStatOutPort.getAllReviewStatsByUserIds(List.of(5L)))
			.thenReturn(List.of(ProjectUserReviewStat.create(5L, 10.0, 2)));
		when(userQueryUseCase.findNamesByUserIds(List.of(5L))).thenReturn(Map.of(5L, "이름"));

		ProjectPromotionDetailResponse result = service.queryProjectPromotionDetail(promotionId, projectId);

		assertEquals(projectId, result.projectId());
		assertEquals(promotionId, result.promotionId());
		assertEquals("제목", result.title());
		assertEquals("홍보 설명", result.description());
		assertEquals(MeetingType.HYBRID, result.meetingType());
		assertEquals(List.of("Go"), result.usedStacks());
		assertEquals("부산", result.meetingPlace());
		assertEquals(3, result.duration());
		assertEquals(1, result.teamMembers().size());
		assertEquals(5L, result.teamMembers().get(0).userId());
		assertEquals(20L, result.teamMembers().get(0).profileId());
		assertEquals("이름", result.teamMembers().get(0).name());
		assertEquals(ProjectRole.OWNER, result.teamMembers().get(0).role());
		assertEquals(ApplyStatus.ACCEPTED, result.teamMembers().get(0).status());
		assertEquals(5.0, result.teamMembers().get(0).score());

		verify(projectPromotionOutPort).findById(promotionId);
		verify(projectOutPort).findById(projectId);
	}
}
