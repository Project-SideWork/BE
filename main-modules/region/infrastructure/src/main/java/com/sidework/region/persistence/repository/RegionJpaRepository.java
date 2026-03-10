package com.sidework.region.persistence.repository;

import com.sidework.region.persistence.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionJpaRepository extends JpaRepository<RegionEntity, Long> {
    @Query("""
            SELECT r FROM RegionEntity r
            WHERE r.parentRegionId is NULL
            """)
    List<RegionEntity> findAllParents();

    @Query("""
            SELECT r FROM RegionEntity r
            WHERE r.parentRegionId = :id
            """)
    List<RegionEntity> findAllByParentId(@Param("id") Long id);

    @Query("""
               SELECT count(r) > 0 FROM RegionEntity r
               WHERE r.id = :id and r.parentRegionId is NOT NULL
            """)
     boolean isSubRegion(@Param("id") Long id);
}
