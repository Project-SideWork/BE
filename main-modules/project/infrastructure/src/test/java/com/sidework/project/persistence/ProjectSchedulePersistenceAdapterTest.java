package com.sidework.project.persistence;

import com.sidework.project.domain.ProjectSchedule;
import com.sidework.project.persistence.adapter.ProjectPersistenceAdapter;
import com.sidework.project.persistence.adapter.ProjectSchedulePersistenceAdapter;
import com.sidework.project.persistence.entity.ProjectScheduleEntity;
import com.sidework.project.persistence.mapper.ProjectMapper;
import com.sidework.project.persistence.mapper.ProjectRecruitPositionMapper;
import com.sidework.project.persistence.mapper.ProjectScheduleMapper;
import com.sidework.project.persistence.repository.ProjectJpaRepository;
import com.sidework.project.persistence.repository.ProjectRecruitPositionJpaRepository;
import com.sidework.project.persistence.repository.ProjectScheduleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectSchedulePersistenceAdapterTest {
    @Mock
    private ProjectScheduleJpaRepository repo;
    private final ProjectScheduleMapper mapper = Mappers.getMapper(ProjectScheduleMapper.class);
    private ProjectSchedulePersistenceAdapter adapter;

    @Captor
    private ArgumentCaptor<List<ProjectScheduleEntity>> captor = ArgumentCaptor.forClass(List.class);

    @BeforeEach
    public void setUp() {
        adapter = new ProjectSchedulePersistenceAdapter(mapper, repo);
    }

    @Test
    void deleteAll은_projectId를_가진_객체를_모두_삭제한다() {
        Long projectId = 1L;

        adapter.deleteAll(projectId);

        verify(repo).deleteAllByProjectId(projectId);
    }

    @Test
    void saveAll은_도메인_객체_배열을_모두_영속성_객체로_변환해_저장한다() {
        List<ProjectSchedule> domains = List.of(
                new ProjectSchedule(null, 1L, "월", 12),
                new ProjectSchedule(null, 1L, "월", 13)
        );

        when(repo.saveAll(any())).thenReturn(List.of());

        adapter.saveAll(domains);

        verify(repo).saveAll(captor.capture());

        List<ProjectScheduleEntity> saved = captor.getValue();

        assertThat(saved).hasSize(2);
        assertThat(saved.getFirst().getProjectId()).isEqualTo(1L);
        assertThat(saved.getFirst().getMeetingDay()).isEqualTo("월");
        assertThat(saved.getFirst().getMeetingHour()).isEqualTo(12);
    }

}
