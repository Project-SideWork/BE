package com.sidework.region.application.dto;

import com.sidework.region.domain.Region;

public record ParentRegionResponse(
        Long id, String sigungu
) {
    public static ParentRegionResponse from(Region region) {
        return new ParentRegionResponse(region.getId(), region.getRegionName());
    }
}
