package com.sidework.region.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    private Long id;

    private String regionName;

    private Long parentRegionId;
}
