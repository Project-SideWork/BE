package com.sidework.region.application.service;

import com.sidework.region.application.dto.RegionResidenceInfo;
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
    public RegionResidenceInfo getRegion(Long id) {
        Region region = repo.findById(id);
        if (region == null) {
            return null;
        }
        Long parentId = region.getParentRegionId();
        Region parent = (parentId == null) ? null : repo.findById(parentId);
        return buildRegionResidenceInfo(region, parent);
    }
    private RegionResidenceInfo buildRegionResidenceInfo(Region region, Region parentRegion) {
        String regionName = region.getRegionName();
        if (parentRegion == null) {
            return new RegionResidenceInfo(
                regionName,
                region.getId(),
                null
            );
        }
        String parentName = parentRegion.getRegionName();
        String display = parentName + " " + regionName;
        return new RegionResidenceInfo(
            display,
            region.getId(),
            parentRegion.getId()
        );
    }
}
