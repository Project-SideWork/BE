package com.sidework.region.application.dto;

import com.sidework.region.domain.Region;

public record SubRegionResponse(
        Long id, String sigungu, Long parentRegionId
) {
    public static SubRegionResponse from(Region region) {
        return new SubRegionResponse(region.getId(), region.getRegionName(), region.getParentRegionId());
    }
}
