package com.datmai.moviereservation.common.dto.response.ticket;

import com.datmai.moviereservation.common.constant.SeatStatus;
import com.datmai.moviereservation.common.dto.response.schedule.FetchScheduleDTO;

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
