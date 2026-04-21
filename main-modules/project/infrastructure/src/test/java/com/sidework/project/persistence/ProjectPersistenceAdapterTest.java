package com.sidework.project.persistence;

import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.ProjectScheduleCommand;
import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.domain.*;
import com.sidework.project.persistence.adapter.ProjectPersistenceAdapter;
import com.sidework.project.persistence.entity.ProjectEntity;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.persistence.mapper.ProjectMapper;
import com.sidework.project.persistence.mapper.ProjectRecruitPositionMapper;
import com.sidework.project.persistence.repository.ProjectJpaRepository;
import com.sidework.project.persistence.repository.ProjectRecruitPositionJpaRepository;
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
    @Mock
    private ProjectRecruitPositionJpaRepository recruitPositionRepo;
    private final ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);
    private final ProjectRecruitPositionMapper recruitPositionMapper = Mappers.getMapper(ProjectRecruitPositionMapper.class);
    private ProjectPersistenceAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new ProjectPersistenceAdapter(repo, recruitPositionRepo, mapper, recruitPositionMapper);
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

    @Test
    void findByIdInDesc는_ids가_비어있으면_빈_리스트를_반환한다() {
        List<Project> result = adapter.findByIdInDesc(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdInDesc는_ids로_프로젝트를_DESC_순으로_조회해_도메인으로_변환한다() {
        // given
        List<Long> ids = List.of(3L, 2L, 1L);

        ProjectEntity entity1 = new ProjectEntity(
                3L,
                1L,
                "프로젝트3",
                "설명3",
                Instant.now(),
                Instant.now(),
                MeetingType.HYBRID,
                ProjectStatus.RECRUITING
        );

        ProjectEntity entity2 = new ProjectEntity(
                2L,
                1L,
                "프로젝트2",
                "설명2",
                Instant.now(),
                Instant.now(),
                MeetingType.ONLINE,
                ProjectStatus.PREPARING
        );

        ProjectEntity entity3 = new ProjectEntity(
                1L,
                1L,
                "프로젝트1",
                "설명1",
                Instant.now(),
                Instant.now(),
                MeetingType.OFFLINE,
                ProjectStatus.CANCELED
        );

        when(repo.findAllByIdsInDesc(ids)).thenReturn(List.of(entity1, entity2, entity3));

        // when
        List<Project> result = adapter.findByIdInDesc(ids);

        // then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(1L, result.get(2).getId());
        verify(repo).findAllByIdsInDesc(ids);
    }

    @Test
    void findByIdInDesc는_ids가_null이면_빈_리스트를_반환한다() {
        List<Project> result = adapter.findByIdInDesc(null);

        assertNotNull(result);

        assertTrue(result.isEmpty());
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
                1L,
                List.of(
                        new ProjectScheduleCommand(MeetingDay.MON, List.of(MeetingHour.HOUR_1,MeetingHour.HOUR_2,MeetingHour.HOUR_3)),
                        new ProjectScheduleCommand(MeetingDay.THU, List.of(MeetingHour.HOUR_1,MeetingHour.HOUR_2,MeetingHour.HOUR_3))
                ),
                List.of(1L, 2L, 3L), // requiredStacks
                List.of(1L, 2L, 3L), // preferredStacks
                ProjectStatus.RECRUITING          // status
        );
    }

    private Project createProject(
            ProjectCommand command
    ) {
        return new Project(
                null,
                1L,
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
