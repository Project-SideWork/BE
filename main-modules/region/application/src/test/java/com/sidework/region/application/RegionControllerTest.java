package com.sidework.region.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.response.exception.ExceptionAdvice;
import com.sidework.region.application.adapter.RegionController;
import com.sidework.region.application.service.RegionQueryService;
import com.sidework.region.domain.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = RegionTestApplication.class)
@Import(ExceptionAdvice.class)
public class RegionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegionQueryService regionQueryService;

    @Test
    void 상위행정구역_조회_성공시_200을_반환한다() throws Exception {
        when(regionQueryService.queryParents()).thenReturn(
                List.of(
                        new Region(4L, "서울특별시", null),
                        new Region(2L, "인천광역시", null),
                        new Region(3L, "경기도", null)
                )
        );

        mockMvc.perform(get("/api/v1/regions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(3))
                .andExpect(jsonPath("$.isSuccess").value(true));

    }

    @Test
    void 하위행정구역_조회_성공시_200을_반환한다() throws Exception {
        Long givenId = 1L;
        when(regionQueryService.queryByParent(givenId)).thenReturn(
                List.of(
                        new Region(4L, "서울특별시", givenId),
                        new Region(2L, "인천광역시", givenId),
                        new Region(3L, "경기도", givenId)
                )
        );

        mockMvc.perform(get("/api/v1/regions/{parentRegionId}", givenId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(3))
                .andExpect(jsonPath("$.isSuccess").value(true));
    }
}
