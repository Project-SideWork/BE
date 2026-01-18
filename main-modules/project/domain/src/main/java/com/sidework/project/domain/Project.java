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

    private String title;

    private String description;

    private LocalDate startDt;

    private LocalDate endDt;

    private MeetingType meetingType;

    private ProjectStatus status;

    public void delete(){
        this.status = ProjectStatus.CANCELED;
    }

    public void update(String title,
                          String description,
                          LocalDate startDt,
                          LocalDate endDt,
                          MeetingType meetingType,
                          ProjectStatus status) {

        this.title = title;
        this.description = description;
        this.startDt = startDt;
        this.endDt = endDt;
        this.meetingType = meetingType;
        this.status = status;
    }
}
