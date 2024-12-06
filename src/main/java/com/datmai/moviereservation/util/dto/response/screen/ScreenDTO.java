package com.datmai.moviereservation.util.dto.response.screen;

import java.time.Instant;
import java.util.List;

import com.datmai.moviereservation.util.constant.ScreenName;
import com.datmai.moviereservation.util.dto.response.schedule.CreateScheduleDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreenDTO {

    private long id;
    private ScreenName name;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    private List<CreateScheduleDTO> schedule;
}
