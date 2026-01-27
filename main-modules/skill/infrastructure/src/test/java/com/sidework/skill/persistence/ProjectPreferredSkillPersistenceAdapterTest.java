package com.sidework.skill.persistence;


import com.sidework.skill.domain.ProjectPreferredSkill;
import com.sidework.skill.persistence.adapter.ProjectPreferredSkillPersistenceAdapter;
import com.sidework.skill.persistence.entity.ProjectPreferredSkillEntity;
import com.sidework.skill.persistence.mapper.ProjectPreferredSkillMapper;
import com.sidework.skill.persistence.repository.ProjectPreferredSkillJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectPreferredSkillPersistenceAdapterTest {
    @Mock
    private ProjectPreferredSkillJpaRepository repo;
    private final ProjectPreferredSkillMapper mapper = Mappers.getMapper(ProjectPreferredSkillMapper.class);
    private ProjectPreferredSkillPersistenceAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new ProjectPreferredSkillPersistenceAdapter(repo, mapper);
    }

    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        ProjectPreferredSkill domain = createDomain();
        ProjectPreferredSkillEntity entity = createEntity();
        when(repo.save(any(ProjectPreferredSkillEntity.class))).thenReturn(entity);

        adapter.save(domain);
        verify(repo).save(any(ProjectPreferredSkillEntity.class));
    }



    private ProjectPreferredSkill createDomain() {
        return new ProjectPreferredSkill(1L, 2L, 3L);
    }

    private ProjectPreferredSkillEntity createEntity() {
        return new ProjectPreferredSkillEntity(1L, 2L, 3L);
    }
}
