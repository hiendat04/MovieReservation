package com.datmai.moviereservation.util.dto.response.ticket;

import java.time.LocalDate;

import com.datmai.moviereservation.util.constant.SeatStatus;
import com.datmai.moviereservation.util.dto.response.schedule.FetchScheduleDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TicketDTO {
    private long id;
    private double price;
    private TicketSeat seat;
    private FetchScheduleDTO schedule;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TicketSeat {
        private long id;
        private String seatIdentifier;
        private SeatStatus status;
    }

}
