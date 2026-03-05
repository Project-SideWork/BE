package com.sidework.region.persistence.adapter;

import com.sidework.region.application.exception.RegionNotFoundException;
import com.sidework.region.application.port.out.RegionOutPort;
import com.sidework.region.domain.Region;
import com.sidework.region.persistence.entity.RegionEntity;
import com.sidework.region.persistence.mapper.RegionMapper;
import com.sidework.region.persistence.repository.RegionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionPersistenceAdapter implements RegionOutPort {
    private final RegionMapper mapper;
    private final RegionJpaRepository repo;


    @Override
    public List<Region> findAllParents() {
        List<RegionEntity> entities = repo.findAllParents();
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Region> findAllByParent(Long id) {
        List<RegionEntity> entities = repo.findAllByParentId(id);
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
