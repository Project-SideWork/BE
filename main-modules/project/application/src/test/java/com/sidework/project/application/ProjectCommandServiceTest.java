package com.sidework.project.application;

import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.application.exception.InvalidCommandException;
import com.sidework.project.application.exception.ProjectDeleteAuthorityException;
import com.sidework.project.application.exception.ProjectNotChangeableException;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.application.port.out.ProjectRecruitPositionOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.SkillLevel;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.service.ProjectCommandService;
import com.sidework.skill.application.port.in.ProjectPreferredSkillCommandUseCase;
import com.sidework.skill.application.port.in.ProjectRequiredCommandUseCase;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static com.sidework.project.domain.ProjectStatus.CANCELED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectCommandServiceTest {
    @Mock
    private ProjectOutPort repo;

    @Mock
    private ProjectUserOutPort projectUserRepo;

    @Mock
    private ProjectRequiredCommandUseCase requiredSkillCommandService;

    @Mock
    private ProjectPreferredSkillCommandUseCase preferredSkillCommandService;

    @Mock
    private ProjectRecruitPositionOutPort projectRecruitPositionRepository;

    @InjectMocks
    ProjectCommandService service;

    @Captor
    ArgumentCaptor<Project> projectArgumentCaptor;

    @Captor
    ArgumentCaptor<List<com.sidework.project.domain.ProjectRecruitPosition>> recruitPositionsCaptor;

    @Test
    void 정상적인_프로젝트_생성_DTO로_프로젝트_생성에_성공한다() {
        ProjectCommand command = createCommand(ProjectStatus.PREPARING);
        when(repo.save(any(Project.class))).thenReturn(1L);
        when(projectUserRepo.queryAllProjectIds(anyLong())).thenReturn(List.of());
        doNothing().when(requiredSkillCommandService).create(anyLong(), any());
        doNothing().when(preferredSkillCommandService).create(anyLong(), any());
        doNothing().when(projectRecruitPositionRepository).saveAll(anyLong(), any());

        service.create(1L, command);

        verify(repo).save(projectArgumentCaptor.capture());
        Project saved = projectArgumentCaptor.getValue();

        assertEquals(command.title(), saved.getTitle());
        assertEquals(command.description(), saved.getDescription());
        assertEquals(command.startDt(), saved.getStartDt());
        assertEquals(command.endDt(), saved.getEndDt());
        assertEquals(command.meetingType(), saved.getMeetingType());
        assertEquals(ProjectStatus.PREPARING, saved.getStatus());
        verify(projectRecruitPositionRepository).saveAll(eq(1L), recruitPositionsCaptor.capture());
        List<com.sidework.project.domain.ProjectRecruitPosition> positions = recruitPositionsCaptor.getValue();
        assertEquals(command.recruitPositions().size(), positions.size());
        assertEquals(command.recruitPositions().get(0).role(), positions.get(0).getRole());
        assertEquals(command.recruitPositions().get(0).headCount(), positions.get(0).getHeadCount());
        assertEquals(command.recruitPositions().get(0).level(), positions.get(0).getLevel());
    }

    @Test
    void 시작일보다_종료일이_빠르면_InvalidCommandException을_던진다() {
        ProjectCommand command = createInvalidDateRangeCommand();
        assertThrows(
                InvalidCommandException.class,
                () -> service.create(1L, command)
        );
    }


    @Test
    void 사용자가_참여하고_있는_프로젝트와_동일한_이름으로_프로젝트_생성시_예외를_발생한다() {
        Long userId = 1L;
        List<Long> projectIds = List.of(1L, 2L);
        List<ProjectTitleDto> titles = List.of(
                new ProjectTitleDto(1L, "버스 실시간 위치 서비스"),
                new ProjectTitleDto(2L, "WebSocket 기반 실시간 위치 공유 프로젝트")
        );
        ProjectCommand command = createCommand(ProjectStatus.PREPARING);
        when(projectUserRepo.queryAllProjectIds(userId)).thenReturn(projectIds);
        when(repo.findAllTitles(projectIds)).thenReturn(titles);

        assertThrows(
            InvalidCommandException.class,
                () -> service.create(userId, command)
        );

    }

    @Test
    void 정상적인_수정_요청_DTO로_프로젝트_수정에_성공한다() {
        Long projectId = 1L;
        ProjectCommand command = createCommand(ProjectStatus.PREPARING);
        ProjectCommand updateCommand = createUpdateCommand();

        Project project = createProject(command);

        when(repo.existsById(projectId)).thenReturn(true);
        when(repo.findById(projectId)).thenReturn(project);
        when(projectUserRepo.queryAllProjectIds(anyLong())).thenReturn(List.of());
        doNothing().when(requiredSkillCommandService).update(anyLong(), any());
        doNothing().when(preferredSkillCommandService).update(anyLong(), any());
        doNothing().when(projectRecruitPositionRepository).deleteByProjectId(anyLong());
        doNothing().when(projectRecruitPositionRepository).saveAll(anyLong(), any());

        service.update(1L, projectId, updateCommand);

        assertNotEquals(command.title(), project.getTitle());
        assertNotEquals(command.description(), project.getDescription());
        assertNotEquals(command.startDt(), project.getStartDt());
        assertNotEquals(command.endDt(), project.getEndDt());
        assertNotEquals(command.meetingType(), project.getMeetingType());
        assertNotEquals(CANCELED, project.getStatus());
        verify(projectRecruitPositionRepository).deleteByProjectId(projectId);
        verify(projectRecruitPositionRepository).saveAll(eq(projectId), recruitPositionsCaptor.capture());
        List<com.sidework.project.domain.ProjectRecruitPosition> positions = recruitPositionsCaptor.getValue();
        assertEquals(updateCommand.recruitPositions().size(), positions.size());
        assertEquals(updateCommand.recruitPositions().get(0).role(), positions.get(0).getRole());
        assertEquals(updateCommand.recruitPositions().get(0).headCount(), positions.get(0).getHeadCount());
        assertEquals(updateCommand.recruitPositions().get(0).level(), positions.get(0).getLevel());
    }

    @Test
    void 수정시_시작일보다_종료일이_빠르면_InvalidCommandException을_던진다() {
        Long projectId = 1L;
        ProjectCommand command = createInvalidDateRangeCommand();

        when(repo.existsById(projectId)).thenReturn(true);
        assertThrows(
                InvalidCommandException.class,
                () -> service.update(1L, projectId, command)
        );
    }

    @Test
    void FINISHED_상태의_프로젝트를_상태_변경시_ProjectNotChangeableException을_던진다() {
        Long projectId = 1L;
        ProjectCommand command = createCommand(ProjectStatus.FINISHED);
        Project project = createProject(command);
        when(repo.existsById(projectId)).thenReturn(true);
        when(repo.findById(projectId)).thenReturn(project);

        assertThrows(
                ProjectNotChangeableException.class,
                () -> service.update(1L, projectId, command)
        );

    }

    @Test
    void CLOSED_상태의_프로젝트를_상태_변경시_ProjectNotChangeableException을_던진다() {
        Long projectId = 1L;
        ProjectCommand command = createCommand(ProjectStatus.CLOSED);
        Project project = createProject(command);
        when(repo.existsById(projectId)).thenReturn(true);
        when(repo.findById(projectId)).thenReturn(project);

        assertThrows(
                ProjectNotChangeableException.class,
                () -> service.update(1L, projectId, command)
        );

    }

    @Test
    void CANCELLED_상태의_프로젝트를_상태_변경시_ProjectNotChangeableException을_던진다() {
        Long projectId = 1L;
        ProjectCommand command = createCommand(CANCELED);
        Project project = createProject(command);
        when(repo.existsById(projectId)).thenReturn(true);
        when(repo.findById(projectId)).thenReturn(project);

        assertThrows(
                ProjectNotChangeableException.class,
                () -> service.update(1L, projectId, command)
        );

    }

    @Test
    void 사용자가_참여하고_있는_프로젝트와_동일한_이름으로_프로젝트_수정시_InvalidCommandException을_발생한다() {
        Long userId = 1L;
        Long projectId = 1L;

        List<Long> projectIds = List.of(1L, 2L);
        List<ProjectTitleDto> titles = List.of(
                new ProjectTitleDto(1L, "WebSocket 기반 실시간 위치 공유 프로젝트"),
                new ProjectTitleDto(2L, "AI 기반 관광 코스 추천 서비스")
        );
        ProjectCommand command = createUpdateCommand();

        when(repo.existsById(projectId)).thenReturn(true);
        when(projectUserRepo.queryAllProjectIds(userId)).thenReturn(projectIds);
        when(repo.findAllTitles(projectIds)).thenReturn(titles);

        assertThrows(
                InvalidCommandException.class,
                () -> service.update(userId, projectId, command)
        );


        verify(repo).existsById(projectId);
        verify(projectUserRepo).queryAllProjectIds(userId);
        verify(repo).findAllTitles(projectIds);

    }

    @Test
    void 현재_수정하고자_하는_프로젝트는_제목이_같아도_예외를_던지지_않는다() {
        Long userId = 1L;
        Long projectId = 1L;
        List<Long> projectIds = List.of(1L, 2L);
        List<ProjectTitleDto> titles = List.of(
                new ProjectTitleDto(1L, "AI 기반 관광 코스 추천 서비스"),
                new ProjectTitleDto(2L, "WebSocket 기반 실시간 위치 공유 프로젝트")
        );
        ProjectCommand command = createUpdateCommand();
        Project project = createSavedProject();
        String beforeDescription = project.getDescription();


        when(repo.existsById(projectId)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(project);
        when(projectUserRepo.queryAllProjectIds(userId)).thenReturn(projectIds);
        when(repo.findAllTitles(projectIds)).thenReturn(titles);
        doNothing().when(requiredSkillCommandService).update(anyLong(), any());
        doNothing().when(preferredSkillCommandService).update(anyLong(), any());
        doNothing().when(projectRecruitPositionRepository).deleteByProjectId(anyLong());
        doNothing().when(projectRecruitPositionRepository).saveAll(anyLong(), any());

        service.update(userId, projectId, command);

        verify(repo).save(projectArgumentCaptor.capture());
        Project saved = projectArgumentCaptor.getValue();

        assertEquals(project.getTitle(), saved.getTitle());
        assertNotEquals(beforeDescription, saved.getDescription());

        verify(repo).existsById(projectId);
        verify(projectUserRepo).queryAllProjectIds(userId);
        verify(repo).findAllTitles(projectIds);
    }

    @Test
    void 존재하는_projectId로_프로젝트_게시글_삭제에_성공한다() {
        Long userId = 1L;
        Long projectId = 1L;
        List<ProjectRole> roles = List.of(ProjectRole.OWNER, ProjectRole.BACKEND);
        Project project = createProject(createCommand(ProjectStatus.RECRUITING));

        when(repo.existsById(projectId)).thenReturn(true);
        when(repo.findById(projectId)).thenReturn(project);
        when(projectUserRepo.queryUserRolesByProject(userId, projectId)).thenReturn(roles);

        service.delete(userId, projectId);

        assertEquals(CANCELED, project.getStatus());
    }

    @Test
    void 존재하지_않는_projectId로_삭제_시도_시_ProjectNotFoundException을_던진다() {
        Long userId = 1L;
        Long projectId = 1L;

        when(repo.existsById(projectId)).thenReturn(false);
        assertThrows(
                ProjectNotFoundException.class,
                () -> service.delete(userId, projectId)
        );
    }

    @Test
    void 삭제_권한이_없는_사용자가_프로젝트_삭제_시도시_예외를_발생한다() {
        Long userId = 2L;
        Long projectId = 1L;
        List<ProjectRole> roles = List.of(ProjectRole.BACKEND);

        when(repo.existsById(projectId)).thenReturn(true);
        when(projectUserRepo.queryUserRolesByProject(userId, projectId)).thenReturn(roles);

        assertThrows(
                ProjectDeleteAuthorityException.class,
                () -> service.delete(userId, projectId)
        );
    }


    private ProjectCommand createCommand(ProjectStatus status) {
        return new ProjectCommand(
                "버스 실시간 위치 서비스",                 // title
                "WebSocket 기반 실시간 위치 공유 프로젝트", // description
                ProjectRole.BACKEND,
                List.of(
                        new RecruitPosition(
                                ProjectRole.BACKEND,
                                1,
                                SkillLevel.JUNIOR
                        ),
                        new RecruitPosition(
                                ProjectRole.FRONTEND,
                                2,
                                SkillLevel.MID
                        )
                ),
                LocalDate.of(2025, 1, 1),   // startDt
                LocalDate.of(2025, 3, 31),  // endDt
                MeetingType.HYBRID,         // meetingType
                "주 2회 온라인, 월 1회 오프라인", // meetingDetail
                List.of(1L, 2L, 3L), // requiredStacks
                List.of(1L, 2L, 3L),           // preferredStacks
                status         // status
        );
    }
    private ProjectCommand createInvalidDateRangeCommand() {
        return new ProjectCommand(
                "버스 실시간 위치 서비스",
                "WebSocket 기반 실시간 위치 공유 프로젝트", // description
                ProjectRole.BACKEND,
                List.of(
                        new RecruitPosition(
                                ProjectRole.BACKEND,
                                1,
                                SkillLevel.JUNIOR
                        ),
                        new RecruitPosition(
                                ProjectRole.FRONTEND,
                                2,
                                SkillLevel.MID
                        )
                ),
                LocalDate.of(2025, 1, 1),   // startDt
                LocalDate.of(2024, 12, 31),  // endDt
                null,         // meetingType
                "주 2회 온라인, 월 1회 오프라인", // meetingDetail
                List.of(1L, 2L, 3L), // requiredStacks
                List.of(1L, 2L, 3L),           // preferredStacks
                ProjectStatus.RECRUITING          // status
        );
    }

    private ProjectCommand createUpdateCommand() {
        return new ProjectCommand(
                "AI 기반 관광 코스 추천 서비스",              // title
                "사용자 위치와 혼잡도를 반영한 여행 코스 추천", // description
                ProjectRole.BACKEND,
                List.of(
                        new RecruitPosition(
                                ProjectRole.BACKEND,
                                2,
                                SkillLevel.MID
                        ),
                        new RecruitPosition(
                                ProjectRole.FRONTEND,
                                1,
                                SkillLevel.JUNIOR
                        )
                ),
                LocalDate.of(2025, 4, 1),   // startDt
                LocalDate.of(2025, 7, 31),  // endDt
                MeetingType.ONLINE,         // meetingType
                "전면 온라인, 필요 시 비동기 협업", // meetingDetail
                List.of(1L, 2L, 3L), // requiredStacks
                List.of(1L, 2L, 3L),           // preferredStacks
                ProjectStatus.PREPARING                         // status
        );
    }
    private Project createProject(
            ProjectCommand command
    ) {
        return new Project(
                null,
                command.title(),
                command.description(),
                command.startDt(),
                command.endDt(),
                command.meetingType(),
                command.status()
        );
    }
    private Project createSavedProject(
    ) {
        return new Project(
                1L,
                "Dummy",
                "Dummy",
                LocalDate.now(),
                LocalDate.now(),
                MeetingType.HYBRID,
                ProjectStatus.PREPARING
        );
    }
}
