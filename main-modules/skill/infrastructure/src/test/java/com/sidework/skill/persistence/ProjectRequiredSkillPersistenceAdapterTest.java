package com.sidework.skill.persistence;

import com.sidework.skill.domain.ProjectPreferredSkill;
import com.sidework.skill.domain.ProjectRequiredSkill;
import com.sidework.skill.persistence.adapter.ProjectRequiredSkillPersistenceAdapter;
import com.sidework.skill.persistence.entity.ProjectPreferredSkillEntity;
import com.sidework.skill.persistence.entity.ProjectRequiredSkillEntity;
import com.sidework.skill.persistence.mapper.ProjectRequiredSkillMapper;
import com.sidework.skill.persistence.repository.ProjectRequiredSkillJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectRequiredSkillPersistenceAdapterTest {
    @Mock
    private ProjectRequiredSkillJpaRepository repo;
    private final ProjectRequiredSkillMapper mapper = Mappers.getMapper(ProjectRequiredSkillMapper.class);
    private ProjectRequiredSkillPersistenceAdapter adapter;
    ArgumentCaptor<List<ProjectRequiredSkillEntity>> captor = ArgumentCaptor.forClass(List.class);

    @BeforeEach
    void setup() {
        adapter = new ProjectRequiredSkillPersistenceAdapter(repo, mapper);
    }

    @Test
    void saveAll는_도메인_객체_배열을_모두_영속성_객체로_변환해_저장한다() {
        ProjectRequiredSkill domain = createDomain();
        ProjectRequiredSkillEntity entity = createEntity();
        when(repo.saveAll(anyList())).thenReturn(List.of(entity));

        adapter.saveAll(List.of(domain));

        verify(repo).saveAll(captor.capture());

        List<ProjectRequiredSkillEntity> savedEntities = captor.getValue();
        assertEquals(1, savedEntities.size());

        ProjectRequiredSkillEntity saved = savedEntities.get(0);
        assertEquals(domain.getSkillId(), saved.getSkillId());
        assertEquals(domain.getProjectId(), saved.getProjectId());
    }

    private ProjectRequiredSkill createDomain() {
        return new ProjectRequiredSkill(1L, 2L, 3L);
    }

    private ProjectRequiredSkillEntity createEntity() {
        return new ProjectRequiredSkillEntity(1L, 2L, 3L);
    }
}
