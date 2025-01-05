package com.datmai.moviereservation.common.dto.response.showtime;

import java.time.LocalTime;

import com.datmai.moviereservation.common.dto.response.schedule.FetchScheduleDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShowTimeDTO {
    private long id;
    private LocalTime time;
    private FetchScheduleDTO schedule;
}
