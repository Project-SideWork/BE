package com.sidework.region.persistence;

import com.sidework.region.domain.Region;
import com.sidework.region.persistence.adapter.RegionPersistenceAdapter;
import com.sidework.region.persistence.entity.RegionEntity;
import com.sidework.region.persistence.mapper.RegionMapper;
import com.sidework.region.persistence.repository.RegionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegionPersistenceAdapterTest {
    private RegionMapper mapper = Mappers.getMapper(RegionMapper.class);

    @Mock
    private RegionJpaRepository repo;

    private RegionPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RegionPersistenceAdapter(mapper, repo);
    }

    @Test
    void findAllParents는_parentRegionId가_null인_값들을_도메인_객체_배열로_변환한다() {
        when(repo.findAllParents()).thenReturn(
                List.of(
                        new RegionEntity(1L, "서울특별시", null),
                        new RegionEntity(2L, "인천광역시", null),
                        new RegionEntity(3L, "경기도", null)
                )
        );
        List<Region> res = adapter.findAllParents();

        assertNull(res.getFirst().getParentRegionId());
        assertNull(res.get(1).getParentRegionId());
        assertNull(res.getLast().getParentRegionId());
        assertEquals(3, res.size());

        verify(repo).findAllParents();
    }

    @Test
    void findAllByParent는_매개로_전달받은_id를_parentRegionId로_가진_값들을_도메인_객체_배열로_변환한다() {
        when(repo.findAllByParentId(1L)).thenReturn(
                List.of(
                        new RegionEntity(4L, "서울특별시", 1L),
                        new RegionEntity(5L, "인천광역시", 1L),
                        new RegionEntity(6L, "경기도", 1L)
                )
        );

        List<Region> res = adapter.findAllByParent(1L);

        assertEquals(1L, res.getFirst().getParentRegionId());
        assertEquals(1L, res.get(1).getParentRegionId());
        assertEquals(1L, res.getLast().getParentRegionId());
        assertEquals(3, res.size());

        verify(repo).findAllByParentId(1L);
    }

    @Test
    void existsById는_id를_가진_객체가_존재하면_true를_반환한다() {
        when(repo.existsById(1L)).thenReturn(true);

        boolean res = adapter.existsById(1L);
        assertTrue(res);

        verify(repo).existsById(1L);
    }

    @Test
    void existsById는_id를_가진_객체가_존재하지않으면_false를_반환한다() {
        when(repo.existsById(1L)).thenReturn(false);

        boolean res = adapter.existsById(1L);
        assertFalse(res);

        verify(repo).existsById(1L);
    }
}
