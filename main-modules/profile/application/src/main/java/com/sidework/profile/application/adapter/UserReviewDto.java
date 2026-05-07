package com.sidework.profile.application.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserReviewDto(
        String projectTitle,
        String comment,
        Double score,
        LocalDate reviewDt
) {
    public static UserReviewDto from(
            String projectTitle,
            String comment,
            Double score,
            LocalDateTime createdAt

    ) {
        return new UserReviewDto(
                projectTitle,
                comment,
                score,
                createdAt.toLocalDate()
        );

    }
}
