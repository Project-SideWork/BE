package com.sidework.region.application.dto;

public record RegionResidenceInfo(
	String name,
	Long regionId,
	Long parentRegionId
) {
}
