package com.sidework.region.application.docs;

import com.sidework.common.response.ApiResponse;
import com.sidework.region.application.dto.ParentRegionResponse;
import com.sidework.region.application.dto.SubRegionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "지역 API")
public interface RegionControllerDocs {

    @Operation(description = "상위 지역 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<ParentRegionResponse>>> getParentRegions();

    @Operation(description = "하위 지역 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상위 지역 없음")
    })
    ResponseEntity<ApiResponse<List<SubRegionResponse>>> getSubRegions(
            @PathVariable("parentRegionId") Long parentRegionId
    );
}
