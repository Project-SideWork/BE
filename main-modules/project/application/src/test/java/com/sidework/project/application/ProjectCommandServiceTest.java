package com.sidework.project.application;

import com.sidework.project.application.exception.InvalidCommandException;
import com.sidework.project.application.exception.ProjectDeleteAuthorityException;
import com.sidework.project.application.exception.ProjectNotChangeableException;
import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.application.port.in.SkillLevel;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.service.ProjectCommandService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectCommandServiceTest {
    @Mock
    private ProjectOutPort repo;

    @Mock
    private ProjectUserOutPort projectUserRepo;

    @InjectMocks
    ProjectCommandService service;

    @Captor
    ArgumentCaptor<Project> projectArgumentCaptor;

    @Test
    void 정상적인_프로젝트_생성_DTO로_프로젝트_생성에_성공한다() {
        ProjectCommand command = createCommand(ProjectStatus.PREPARING);
        service.create(command);

        verify(repo).save(projectArgumentCaptor.capture());

        Project saved = projectArgumentCaptor.getValue();

        assertEquals(command.title(), saved.getTitle());
        assertEquals(command.description(), saved.getDescription());
        assertEquals(command.startDt(), saved.getStartDt());
        assertEquals(command.endDt(), saved.getEndDt());
        assertEquals(command.meetingType(), saved.getMeetingType());
        assertEquals(ProjectStatus.PREPARING, saved.getStatus());
    }

    @Test
    void 시작일보다_종료일이_빠르면_InvalidCommandException을_던진다() {
        ProjectCommand command = createInvalidCommand();
        assertThrows(
                InvalidCommandException.class,
                () -> service.create(command)
        );
    }


    @Test
    void 사용자가_참여하고_있는_프로젝트와_동일한_이름으로_프로젝트_생성시_예외를_발생한다() {
        Long userId = 1L;
        List<Long> projectIds = List.of(1L, 2L);
        List<String> titles = List.of("버스 실시간 위치 서비스", "WebSocket 기반 실시간 위치 공유 프로젝트");
        ProjectCommand command = createCommand(ProjectStatus.PREPARING);
        when(projectUserRepo.queryAllProjectIds(userId)).thenReturn(projectIds);
        when(repo.findAllTitles(projectIds)).thenReturn(titles);

        assertThrows(
            InvalidCommandException.class,
                () -> service.create(command)
        );

    }

    @Test
    void 정상적인_수정_요청_DTO로_프로젝트_수정에_성공한다() {
        Long projectId = 1L;
        ProjectCommand command = createCommand(ProjectStatus.PREPARING);
        ProjectCommand updateCommand = createUpdateCommand();

        Project project = createProject(command);
        when(repo.findById(projectId)).thenReturn(project);

        service.update(projectId, updateCommand);

        assertNotEquals(command.title(), project.getTitle());
        assertNotEquals(command.description(), project.getDescription());
        assertNotEquals(command.startDt(), project.getStartDt());
        assertNotEquals(command.endDt(), project.getEndDt());
        assertNotEquals(command.meetingType(), project.getMeetingType());
        assertNotEquals(CANCELED, project.getStatus());
    }

    @Test
    void 수정시_시작일보다_종료일이_빠르면_InvalidCommandException을_던진다() {
        Long projectId = 1L;
        ProjectCommand command = createInvalidCommand();
        assertThrows(
                InvalidCommandException.class,
                () -> service.update(projectId, command)
        );
    }

    @Test
    void FINISHED_상태의_프로젝트를_상태_변경시_ProjectNotChangeableException을_던진다() {
        Long projectId = 1L;
        ProjectCommand command = createCommand(ProjectStatus.FINISHED);
        Project project = createProject(command);
        when(repo.findById(projectId)).thenReturn(project);
        assertThrows(
                ProjectNotChangeableException.class,
                () -> service.update(projectId, command)
        );

    }

    @Test
    void CLOSED_상태의_프로젝트를_상태_변경시_ProjectNotChangeableException을_던진다() {
        Long projectId = 1L;
        ProjectCommand command = createCommand(ProjectStatus.CLOSED);
        Project project = createProject(command);
        when(repo.findById(projectId)).thenReturn(project);
        assertThrows(
                ProjectNotChangeableException.class,
                () -> service.update(projectId, command)
        );

    }

    @Test
    void CANCELLED_상태의_프로젝트를_상태_변경시_ProjectNotChangeableException을_던진다() {
        Long projectId = 1L;
        ProjectCommand command = createCommand(CANCELED);
        Project project = createProject(command);
        when(repo.findById(projectId)).thenReturn(project);
        assertThrows(
                ProjectNotChangeableException.class,
                () -> service.update(projectId, command)
        );

    }

    @Test
    void 사용자가_참여하고_있는_프로젝트와_동일한_이름으로_프로젝트_수정시_InvalidCommandException을_발생한다() {
        Long userId = 1L;
        Long projectId = 1L;
        List<Long> projectIds = List.of(1L, 2L);
        List<String> titles = List.of("버스 실시간 위치 서비스", "WebSocket 기반 실시간 위치 공유 프로젝트");
        ProjectCommand command = createCommand(ProjectStatus.PREPARING);
        when(projectUserRepo.queryAllProjectIds(userId)).thenReturn(projectIds);
        when(repo.findAllTitles(projectIds)).thenReturn(titles);

        assertThrows(
                InvalidCommandException.class,
                () -> service.update(projectId, command)
        );

    }

    @Test
    void 존재하는_projectId로_프로젝트_게시글_삭제에_성공한다() {
        Long userId = 1L;
        Long projectId = 1L;
        List<ProjectRole> roles = List.of(ProjectRole.OWNER, ProjectRole.BACKEND);
        Project project = createProject(createCommand(ProjectStatus.RECRUITING));

        when(repo.findById(projectId)).thenReturn(project);
        when(projectUserRepo.queryUserRolesByProject(userId, projectId)).thenReturn(roles);

        service.delete(userId, projectId);

        assertEquals(CANCELED, project.getStatus());
    }

    @Test
    void 삭제_권한이_없는_사용자가_프로젝트_삭제_시도시_예외를_발생한다() {
        Long userId = 2L;
        Long projectId = 1L;
        List<ProjectRole> roles = List.of(ProjectRole.BACKEND);

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
                List.of("Spring Boot", "MySQL"), // requiredStacks
                List.of("Redis", "Kafka"),       // preferredStacks
                status         // status
        );
    }

    private ProjectCommand createInvalidCommand() {
        return new ProjectCommand(
                null,
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
                                0,
                                SkillLevel.MID
                        )
                ),
                LocalDate.of(2025, 1, 1),   // startDt
                LocalDate.of(2024, 12, 31),  // endDt
                null,         // meetingType
                "주 2회 온라인, 월 1회 오프라인", // meetingDetail
                List.of("Spring Boot", "MySQL"), // requiredStacks
                List.of("Redis", "Kafka"),       // preferredStacks
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
                List.of("Spring Boot", "MongoDB", "GraphQL"), // requiredStacks
                List.of("Redis", "Docker", "AWS"),            // preferredStacks
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
}
