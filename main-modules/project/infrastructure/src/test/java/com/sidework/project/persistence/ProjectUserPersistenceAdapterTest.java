package com.sidework.project.persistence;

import com.sidework.project.domain.ProjectUserReview;
import com.sidework.project.persistence.adapter.ProjectUserReviewPersistenceAdapter;
import com.sidework.project.persistence.entity.ProjectUserReviewEntity;
import com.sidework.project.persistence.mapper.ProjectUserReviewMapper;
import com.sidework.project.persistence.repository.custom.ProjectUserReviewJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectUserPersistenceAdapterTest {

    @Mock
    private ProjectUserReviewJpaRepository repository;

    private final ProjectUserReviewMapper mapper =
            Mappers.getMapper(ProjectUserReviewMapper.class);

    private ProjectUserReviewPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProjectUserReviewPersistenceAdapter(repository, mapper);
    }

    @Test
    void exists는_프로젝트ID_리뷰어ID_리뷰대상ID로_리뷰_존재여부를_반환한다() {
        Long projectId = 1L;
        Long reviewerUserId = 2L;
        Long revieweeUserId = 3L;

        when(repository.existsByProjectIdAndReviewerUserIdAndRevieweeUserId(
                projectId,
                reviewerUserId,
                revieweeUserId
        )).thenReturn(true);

        boolean result = adapter.exists(projectId, reviewerUserId, revieweeUserId);

        assertTrue(result);

        verify(repository).existsByProjectIdAndReviewerUserIdAndRevieweeUserId(
                projectId,
                reviewerUserId,
                revieweeUserId
        );
    }

    @Test
    void saveAll은_도메인_리스트를_엔티티_리스트로_변환해_저장한다() {
        List<ProjectUserReview> reviews = List.of(
                createReview(1L),
                createReview(2L)
        );

        adapter.saveAll(reviews);

        verify(repository).saveAll(anyList());
    }

    @Test
    void getReviewsByUserIdAndProjectIds는_조회된_리뷰_엔티티를_도메인으로_변환해_반환한다() {
        Long userId = 1L;
        List<Long> projectIds = List.of(1L, 2L);

        List<ProjectUserReviewEntity> entities = List.of(
                createReviewEntity(1L),
                createReviewEntity(2L)
        );

        when(repository.findAllByProjectIdsAndRevieweeUserId(projectIds, userId))
                .thenReturn(entities);

        List<ProjectUserReview> result =
                adapter.getReviewsByUserIdAndProjectIds(userId, projectIds);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(repository).findAllByProjectIdsAndRevieweeUserId(projectIds, userId);
    }

    @Test
    void getReviewsByUserIdAndProjectIds는_조회결과가_null이면_빈_리스트를_반환한다() {
        Long userId = 1L;
        List<Long> projectIds = List.of(1L, 2L);

        when(repository.findAllByProjectIdsAndRevieweeUserId(projectIds, userId))
                .thenReturn(null);

        List<ProjectUserReview> result =
                adapter.getReviewsByUserIdAndProjectIds(userId, projectIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAllByProjectIdsAndRevieweeUserId(projectIds, userId);
    }

    @Test
    void getReviewsByUserIdAndProjectIds는_조회결과가_비어있으면_빈_리스트를_반환한다() {
        Long userId = 1L;
        List<Long> projectIds = List.of(1L, 2L);

        when(repository.findAllByProjectIdsAndRevieweeUserId(projectIds, userId))
                .thenReturn(List.of());

        List<ProjectUserReview> result =
                adapter.getReviewsByUserIdAndProjectIds(userId, projectIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAllByProjectIdsAndRevieweeUserId(projectIds, userId);
    }

    @Test
    void getReviewsByUserId는_사용자ID와_Pageable로_리뷰를_조회한다() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<ProjectUserReviewEntity> entities = List.of(
                createReviewEntity(1L),
                createReviewEntity(2L)
        );

        when(repository.findAllByRevieweeUserId(userId, pageable))
                .thenReturn(entities);

        List<ProjectUserReview> result =
                adapter.getReviewsByUserId(userId, pageable);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(repository).findAllByRevieweeUserId(userId, pageable);
    }

    @Test
    void getReviewsByUserId는_조회결과가_null이면_빈_리스트를_반환한다() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAllByRevieweeUserId(userId, pageable))
                .thenReturn(null);

        List<ProjectUserReview> result =
                adapter.getReviewsByUserId(userId, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAllByRevieweeUserId(userId, pageable);
    }

    @Test
    void getReviewsByUserId는_조회결과가_비어있으면_빈_리스트를_반환한다() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAllByRevieweeUserId(userId, pageable))
                .thenReturn(List.of());

        List<ProjectUserReview> result =
                adapter.getReviewsByUserId(userId, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAllByRevieweeUserId(userId, pageable);
    }

    @Test
    void findReviewCountByUserId는_사용자가_받은_리뷰_개수를_반환한다() {
        Long userId = 1L;

        when(repository.findReviewCountByUserId(userId))
                .thenReturn(3L);

        Long result = adapter.findReviewCountByUserId(userId);

        assertEquals(3L, result);

        verify(repository).findReviewCountByUserId(userId);
    }

    @Test
    void pageReviewByUserId는_사용자ID와_Pageable로_리뷰를_조회한다() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<ProjectUserReviewEntity> entities = List.of(
                createReviewEntity(1L),
                createReviewEntity(2L)
        );

        when(repository.findReviewByUserId(userId, pageable))
                .thenReturn(entities);

        List<ProjectUserReview> result =
                adapter.pageReviewByUserId(userId, pageable);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(repository).findReviewByUserId(userId, pageable);
    }

    @Test
    void pageReviewByUserId는_조회결과가_null이면_빈_리스트를_반환한다() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findReviewByUserId(userId, pageable))
                .thenReturn(null);

        List<ProjectUserReview> result =
                adapter.pageReviewByUserId(userId, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findReviewByUserId(userId, pageable);
    }

    @Test
    void pageReviewByUserId는_조회결과가_비어있으면_빈_리스트를_반환한다() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findReviewByUserId(userId, pageable))
                .thenReturn(List.of());

        List<ProjectUserReview> result =
                adapter.pageReviewByUserId(userId, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findReviewByUserId(userId, pageable);
    }

    private ProjectUserReview createReview(Long id) {
        return ProjectUserReview.builder()
                .id(id)
                .projectId(1L)
                .reviewerUserId(2L)
                .revieweeUserId(3L)
                .responsibility(5)
                .communication(4)
                .collaboration(5)
                .problemSolving(4)
                .comment("좋은 팀원이었습니다.")
                .createdAt(LocalDate.now())
                .build();
    }
    private ProjectUserReviewEntity createReviewEntity(Long id) {
        return ProjectUserReviewEntity.builder()
                .id(id)
                .projectId(1L)
                .reviewerUserId(2L)
                .revieweeUserId(3L)
                .responsibility(5)
                .communication(4)
                .collaboration(5)
                .problemSolving(4)
                .comment("좋은 팀원이었습니다.")
                .build();
    }
}