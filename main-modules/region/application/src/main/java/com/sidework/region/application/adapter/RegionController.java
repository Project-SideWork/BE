package com.sidework.region.application.adapter;

import com.sidework.common.response.ApiResponse;
import com.sidework.region.application.dto.ParentRegionResponse;
import com.sidework.region.application.dto.SubRegionResponse;
import com.sidework.region.application.docs.RegionControllerDocs;
import com.sidework.region.application.port.in.RegionQueryUseCase;
import com.sidework.region.domain.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController implements RegionControllerDocs {
    private final RegionQueryUseCase regionQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParentRegionResponse>>> getParentRegions() {
        List<Region> domains = regionQueryService.queryParents();
        List<ParentRegionResponse> responses = domains.stream().map(ParentRegionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.onSuccess(responses));
    }

    @GetMapping("/{parentRegionId}")
    public ResponseEntity<ApiResponse<List<SubRegionResponse>>> getSubRegions(
            @PathVariable("parentRegionId") Long parentRegionId) {
        List<Region> domains = regionQueryService.queryByParent(parentRegionId);
        List<SubRegionResponse> responses = domains.stream().map(SubRegionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.onSuccess(responses));
    }
}
