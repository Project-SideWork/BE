package com.sidework.region.application.port.out;

import com.sidework.region.domain.Region;

import java.util.List;

public interface RegionOutPort {
    List<Region> findAllParents();
    List<Region> findAllByParent(Long id);
}
