package com.sidework.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private Long id;

    private Long meetRegionId;

    private String title;

    private String description;

    private LocalDate startDt;

    private LocalDate endDt;

    private MeetingType meetingType;

    private ProjectStatus status;

    public static Project create(Long regionId,
                                 String title,
                                 String description,
                                 LocalDate startDt,
                                 LocalDate endDt,
                                 MeetingType meetingType) {
        return Project.builder()
                .meetRegionId(regionId)
                .title(title)
                .description(description)
                .startDt(startDt)
                .endDt(endDt)
                .meetingType(meetingType)
                .status(ProjectStatus.PREPARING)
                .build();
    }

    public void delete(){
        this.status = ProjectStatus.CANCELED;
    }

    public void update(Long regionId,
                       String title,
                       String description,
                       LocalDate startDt,
                       LocalDate endDt,
                       MeetingType meetingType,
                       ProjectStatus status) {
        this.meetRegionId = regionId;
        this.title = title;
        this.description = description;
        this.startDt = startDt;
        this.endDt = endDt;
        this.meetingType = meetingType;
        this.status = status;
    }
}
