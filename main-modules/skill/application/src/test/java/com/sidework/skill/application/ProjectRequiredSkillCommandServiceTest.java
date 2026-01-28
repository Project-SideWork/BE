package com.sidework.skill.application;


import com.sidework.common.exception.InvalidCommandException;
import com.sidework.skill.application.port.out.ProjectRequiredSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.application.service.ProjectRequiredSkillCommandService;
import com.sidework.skill.domain.ProjectRequiredSkill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectRequiredSkillCommandServiceTest {
    @Mock
    private ProjectRequiredSkillOutPort repo;

    @Mock
    private SkillOutPort skillRepo;

    @InjectMocks
    ProjectRequiredSkillCommandService service;

    @Captor
    ArgumentCaptor<List<ProjectRequiredSkill>> captor;

    @Captor
    ArgumentCaptor<List<Long>> idsCaptor;

    @Test
    void 정상적인_프로젝트_우대기술_생성_DTO로_프로젝트_생성에_성공한다() {
        List<Long> command = createCommand();
        when(skillRepo.findActiveSkillsByIdIn(command)).thenReturn(command);
        service.create(1L, command);

        verify(repo).saveAll(captor.capture());

        List<ProjectRequiredSkill> saved = captor.getValue();
        assertEquals(3, saved.size());
    }

    @Test
    void DTO에_DB에_존재하지_않는_ID가_있으면_InvalidCommandException을_던진다() {
        List<Long> command = createCommand();
        when(skillRepo.findActiveSkillsByIdIn(command)).thenReturn(List.of(1L, 3L));
        assertThrows(
                InvalidCommandException.class, () -> service.create(1L, command)
        );
        verify(skillRepo).findActiveSkillsByIdIn(command);
    }

    @Test
    void 프로젝트_생성시_카테고리에_해당하는_ID가_포함되면_InvalidCommandException을_던진다() {
        List<Long> command = createCommand();
        when(skillRepo.findActiveSkillsByIdIn(command)).thenReturn(List.of(1L, 2L));

        assertThrows(
                InvalidCommandException.class,
                () -> service.create(1L, command)
        );

        verify(skillRepo).findActiveSkillsByIdIn(command);
    }

    @Test
    void 프로젝트_수정시_신규기술_추가에_성공한다() {
        List<Long> command = createCommand();
        when(skillRepo.findActiveSkillsByIdIn(List.of(1L, 2L, 3L))).thenReturn(List.of(1L,2L,3L));
        when(repo.findAllSkillIdsByProject(1L)).thenReturn(List.of(1L, 2L));
        service.update(1L, command);

        verify(repo).saveAll(captor.capture());

        List<ProjectRequiredSkill> saved = captor.getValue();
        assertEquals(1, saved.size());
    }

    @Test
    void 프로젝트_수정시_요청에_미포함된_기존_기술_삭제에_성공한다() {
        List<Long> command = createDeleteCommand();
        when(skillRepo.findActiveSkillsByIdIn(List.of(1L, 2L))).thenReturn(List.of(1L,2L));
        when(repo.findAllSkillIdsByProject(1L)).thenReturn(List.of(1L, 2L, 3L));
        service.update(1L, command);

        verify(repo).deleteByProjectIdAndSkillIdIn(eq(1L), idsCaptor.capture());

        List<Long> deleted = idsCaptor.getValue();
        assertEquals(1, deleted.size());
    }

    @Test
    void 프로젝트_수정시_카테고리에_해당하는_ID가_포함되면_InvalidCommandException을_던진다() {
        List<Long> command = createCommand();
        when(skillRepo.findActiveSkillsByIdIn(command)).thenReturn(List.of(1L, 2L));

        assertThrows(
                InvalidCommandException.class,
                () -> service.update(1L, command)
        );

        verify(skillRepo).findActiveSkillsByIdIn(command);
    }

    private List<Long> createCommand() {
        return List.of(1L, 2L, 3L);
    }

    private List<Long> createDeleteCommand() {
        return List.of(1L, 2L);
    }
}
