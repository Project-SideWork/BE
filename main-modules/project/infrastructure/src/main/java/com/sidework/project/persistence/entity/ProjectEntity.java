package com.sidework.project.persistence.entity;


import com.sidework.common.entity.BaseEntity;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;           // 프로젝트 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;     // 프로젝트 소개

    @Column(nullable = false)
    private Instant startDt;       // 시작일자

    @Column(nullable = false)
    private Instant endDt;         // 종료일자

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private MeetingType meetingType;      // 대면, 비대면, 혼합

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private ProjectStatus status;           // 모집중, 모집완료, 준비, 취소
}
