package com.sidework.project.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_schedules")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;

    private String meetingDay;

    private Integer meetingHour;
}
