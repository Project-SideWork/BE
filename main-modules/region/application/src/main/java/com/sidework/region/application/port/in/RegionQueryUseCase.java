package com.sidework.region.application.port.in;

import com.sidework.region.domain.Region;

import java.util.List;

public interface RegionQueryUseCase {
    List<Region> queryParents();
    List<Region> queryByParent(Long id);
}
