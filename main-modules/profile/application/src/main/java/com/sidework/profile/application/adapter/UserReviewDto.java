package com.sidework.profile.application.adapter;

import java.time.LocalDate;

public record UserReviewDto(
        String title,
        String comment,
        Double score,
        LocalDate reviewDt
) {
}
