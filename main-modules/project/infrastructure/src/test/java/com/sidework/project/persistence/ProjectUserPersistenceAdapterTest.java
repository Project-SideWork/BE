package com.sidework.project.persistence;

import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectUser;
import com.sidework.project.persistence.adapter.ProjectUserPersistenceAdapter;
import com.sidework.project.persistence.entity.ProjectUserEntity;
import com.sidework.project.persistence.mapper.ProjectUserMapper;
import com.sidework.project.persistence.repository.ProjectUserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectUserPersistenceAdapterTest {
    @Mock
    private ProjectUserJpaRepository repo;

    private final ProjectUserMapper mapper = Mappers.getMapper(ProjectUserMapper.class);
    private ProjectUserPersistenceAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new ProjectUserPersistenceAdapter(repo, mapper);
    }

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        ProjectUser domain = createProjectUser();
        ProjectUserEntity entity = createProjectUserEntity();
        when(repo.save(any(ProjectUserEntity.class))).thenReturn(entity);

        adapter.save(domain);

        verify(repo).save(any(ProjectUserEntity.class));
    }

    @Test
    void queryAllProjectIds는_사용자가_참여중인_모든_프로젝트_ID를_반환한다() {
        List<Long> projectIds = List.of(1L, 2L);
        Long userId = 1L;

        when(repo.findAllIdsByUserId(userId)).thenReturn(projectIds);

        List<Long> myProjects = adapter.queryAllProjectIds(userId);

        assertEquals(projectIds, myProjects);
        verify(repo).findAllIdsByUserId(userId);
    }

    public static ProjectUser createProjectUser() {
        return ProjectUser.create(
                1L,
                1L,
                ApplyStatus.UNREAD,
                ProjectRole.BACKEND
        );
    }

    public static ProjectUserEntity createProjectUserEntity() {
        return ProjectUserEntity.builder()
                .projectId(1L)
                .userId(1L)
                .status(ApplyStatus.UNREAD)
                .role(ProjectRole.BACKEND)
                .build();
    }
}
