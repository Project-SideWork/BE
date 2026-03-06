package com.sidework.region.application;

import com.sidework.region.application.port.out.RegionOutPort;
import com.sidework.region.application.service.RegionQueryService;
import com.sidework.region.domain.Region;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RegionQueryServiceTest {
    @Mock
    private RegionOutPort repo;

    @InjectMocks
    private RegionQueryService service;

    @Test
    void queryParents는_부모지역코드가_null인_행정구역을_조회한다() {
        when(repo.findAllParents()).thenReturn(
                List.of(
                        new Region(1L, "서울특별시", null),
                        new Region(2L, "인천광역시", null),
                        new Region(3L, "경기도", null)
                )
        );
        List<Region> regions = service.queryParents();

        assertThat(regions).hasSize(3);
        verify(repo).findAllParents();
    }

    @Test
    void queryByParent는_id를_부모지역코드로_갖는_구역을_조회한다() {
        Long givenId = 1L;
        when(repo.findAllByParent(givenId)).thenReturn(
                List.of(
                        new Region(4L, "서울특별시", givenId),
                        new Region(2L, "인천광역시", givenId),
                        new Region(3L, "경기도", givenId)
                )
        );
        List<Region> regions = service.queryByParent(givenId);

        assertThat(regions).hasSize(3);
        verify(repo).findAllByParent(givenId);
    }
}
