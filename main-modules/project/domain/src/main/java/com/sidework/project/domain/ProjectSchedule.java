package com.sidework.project.domain;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"projectId", "meetingDay", "meetingHour"})
public class ProjectSchedule {
    private Long id;
    private Long projectId;
    private String meetingDay;
    private Integer meetingHour;

    public static ProjectSchedule create(Long projectId, String meetingDay, Integer meetingHour) {
        return ProjectSchedule.builder()
                .projectId(projectId)
                .meetingDay(meetingDay)
                .meetingHour(meetingHour)
                .build();
    }
}
