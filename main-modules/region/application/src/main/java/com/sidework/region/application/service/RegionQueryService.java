package com.sidework.region.application.service;

import com.sidework.region.application.port.in.RegionQueryUseCase;
import com.sidework.region.application.port.out.RegionOutPort;
import com.sidework.region.domain.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionQueryService implements RegionQueryUseCase {
    private final RegionOutPort repo;

    @Override
    public List<Region> queryParents() {
        return repo.findAllParents();
    }

    @Override
    public List<Region> queryByParent(Long id) {
        return repo.findAllByParent(id);
    }

    @Override
    public String getRegion(Long id) {
        Region region = repo.findById(id);
        Region parentRegion = repo.findById(region.getParentRegionId());
        return parentRegion.getRegionName()+ " " + region.getRegionName();

    }
}
