package com.sidework.region.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "regions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionEntity {
    @Id
    private Long id;

    private String regionName;

    private Long parentRegionId;
}
