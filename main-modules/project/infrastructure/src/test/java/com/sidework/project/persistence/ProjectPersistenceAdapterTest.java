package com.sidework.project.persistence;

import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.SkillLevel;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.persistence.adapter.ProjectPersistenceAdapter;
import com.sidework.project.persistence.entity.ProjectEntity;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.persistence.mapper.ProjectMapper;
import com.sidework.project.persistence.repository.ProjectJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectPersistenceAdapterTest {
    @Mock
    private ProjectJpaRepository repo;
    private final ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);
    private ProjectPersistenceAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new ProjectPersistenceAdapter(repo, mapper);
    }

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        Project domain = createProject(createCommand());
        ProjectEntity entity = createProjectEntity();
        when(repo.save(any(ProjectEntity.class))).thenReturn(entity);

        Long id = adapter.save(domain);

        assertEquals(1L, id);
        verify(repo).save(any(ProjectEntity.class));
    }

    @Test
    void findById는_Id로_프로젝트를_조회해_도메인_객체로_변환한다() {
        ProjectEntity entity = createProjectEntity();
        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        Project project = adapter.findById(1L);

        assertNotNull(project);
        assertEquals(1L, project.getId());

        verify(repo).findById(1L);
    }

    @Test
    void findById로_존재하지_않는_프로젝트_조회_시_ProjectNotFoundException을_던진다(){
        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class,
                () -> adapter.findById(2L));

        verify(repo).findById(2L);
    }

    private ProjectCommand createCommand() {
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
                List.of(1L, 2L, 3L), // preferredStacks
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
                List.of(1L, 2L, 3L), // preferredStacks
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
    private ProjectEntity createProjectEntity() {
        return new ProjectEntity(
                1L,
                "버스 실시간 위치 서비스",              // title
                "사용자 위치와 혼잡도를 반영한 여행 코스 추천", // description
                Instant.now(),
                Instant.now(),
                MeetingType.HYBRID,
                ProjectStatus.CANCELED
        );
    }
}
