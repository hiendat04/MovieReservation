package com.datmai.moviereservation.common.dto.response.schedule;

import java.time.LocalDate;

import com.datmai.moviereservation.common.constant.MovieGenre;
import com.datmai.moviereservation.common.constant.ScreenName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FetchScheduleDTO {
    private LocalDate date;
    private ScheduleMovie movie;
    private ScheduleScreen screen;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleMovie {
        private long id;
        private String name;
        private String poster;
        private MovieGenre genre;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleScreen {
        private long id;
        private ScreenName name;
    }
}
