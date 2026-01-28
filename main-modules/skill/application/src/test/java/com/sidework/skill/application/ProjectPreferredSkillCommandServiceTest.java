package com.sidework.skill.application;

import com.sidework.common.exception.InvalidCommandException;
import com.sidework.skill.application.port.out.ProjectPreferredSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.application.service.ProjectPreferredSkillCommandService;
import com.sidework.skill.domain.ProjectPreferredSkill;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectPreferredSkillCommandServiceTest {
    @Mock
    private ProjectPreferredSkillOutPort repo;

    @Mock
    private SkillOutPort skillRepo;

    @InjectMocks
    ProjectPreferredSkillCommandService service;

    @Captor
    ArgumentCaptor<List<ProjectPreferredSkill>> captor;

    @Test
    void 정상적인_프로젝트_우대기술_생성_DTO로_프로젝트_생성에_성공한다() {
        List<Long> command = createCommand();
        when(skillRepo.existsById(1L)).thenReturn(true);
        when(skillRepo.existsById(2L)).thenReturn(true);
        when(skillRepo.existsById(3L)).thenReturn(true);
        service.create(1L, command);

        verify(repo).saveAll(captor.capture());

        List<ProjectPreferredSkill> saved = captor.getValue();
        assertEquals(3, saved.size());
    }

    @Test
    void DTO에_DB에_존재하지_않는_ID가_있으면_InvalidCommandException을_던진다() {
        List<Long> command = createCommand();
        when(skillRepo.existsById(1L)).thenReturn(true);
        when(skillRepo.existsById(2L)).thenReturn(true);
        when(skillRepo.existsById(3L)).thenReturn(false);
        assertThrows(
                InvalidCommandException.class, () -> service.create(1L, command)
        );
    }

    @Test
    void 프로젝트_수정시_신규기술_추가에_성공한다() {
        List<Long> command = createCommand();
        when(skillRepo.findIdsByIdIn(List.of(1L, 2L, 3L))).thenReturn(List.of(1L,2L,3L));
        when(repo.findAllSkillIdsByProject(1L)).thenReturn(List.of(1L, 2L));
        service.update(1L, command);

        verify(repo).saveAll(captor.capture());

        List<ProjectPreferredSkill> saved = captor.getValue();
        assertEquals(1, saved.size());
    }

    @Test
    void 프로젝트_수정시_요청에_미포함된_기존_기술_삭제에_성공한다() {
        List<Long> command = createDeleteCommand();
        when(skillRepo.findIdsByIdIn(List.of(1L, 2L))).thenReturn(List.of(1L,2L));
        when(repo.findAllSkillIdsByProject(1L)).thenReturn(List.of(1L, 2L, 3L));
        service.update(1L, command);

        verify(repo).deleteAll(captor.capture());

        List<ProjectPreferredSkill> deleted = captor.getValue();
        assertEquals(1, deleted.size());
    }

    private List<Long> createCommand() {
        return List.of(1L, 2L, 3L);
    }

    private List<Long> createDeleteCommand() {
        return List.of(1L, 2L);
    }
}
