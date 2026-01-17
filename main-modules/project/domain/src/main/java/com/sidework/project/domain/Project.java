package com.sidework.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
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
}
