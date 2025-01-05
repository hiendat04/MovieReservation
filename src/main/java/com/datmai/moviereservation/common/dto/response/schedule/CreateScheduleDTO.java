package com.datmai.moviereservation.common.dto.response.schedule;

import java.time.Instant;
import java.time.LocalDate;

import com.datmai.moviereservation.common.constant.ScreenName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleDTO {
    private long id;
    private LocalDate date;
    private Instant createdAt;
    private String createdBy;
    private ScheduleScreen screen;
    private ScheduleMovie movie;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleScreen {
        private long id;
        private ScreenName name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleMovie {
        private long id;
        private String name;
    }
}