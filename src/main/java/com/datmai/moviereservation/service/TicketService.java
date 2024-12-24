package com.datmai.moviereservation.service;

import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Seat;
import com.datmai.moviereservation.domain.Ticket;
import com.datmai.moviereservation.repository.TicketRepository;
import com.datmai.moviereservation.util.constant.SeatStatus;
import com.datmai.moviereservation.util.dto.response.ticket.TicketDTO;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ScheduleService scheduleService;
    private final SeatService seatService;

    public TicketService(TicketRepository ticketRepository, ScheduleService scheduleService, SeatService seatService) {
        this.ticketRepository = ticketRepository;
        this.scheduleService = scheduleService;
        this.seatService = seatService;
    }

    public TicketDTO createTicket(Ticket ticket) {
        // Update seat status available
        ticket.getSeat().setStatus(SeatStatus.AVAILABLE);

        // Get the price corresponding to screen format if ticket price is null
        if (ticket.getPrice() == 0.0) {
            // Get the schedule
            Schedule ticketSchedule = this.scheduleService.fetchScheduleById(ticket.getSchedule().getId());
            double price = ticketSchedule.getScreen().getFormat().getPrice();
            ticket.setPrice(price);
        }

        // Save ticket and return its DTO
        Ticket newTicket = this.ticketRepository.save(ticket);
        TicketDTO ticketDTO = this.convertTicketDTO(newTicket);
        return ticketDTO;
    }

    public boolean isTicketExist(Schedule schedule, Seat seat) {
        return this.ticketRepository.existsByScheduleAndSeat(schedule, seat);
    }

    public TicketDTO convertTicketDTO(Ticket ticket) {
        // Fetch related schedule and seat
        Schedule schedule = this.scheduleService.fetchScheduleById(ticket.getSchedule().getId());
        Seat seat = this.seatService.fetchSeatById(ticket.getSeat().getId());

        // Convert DTO
        TicketDTO res = new TicketDTO(ticket.getId(), ticket.getPrice(),
                new TicketDTO.TicketSeat(seat.getId(), seat.getSeatIdentifier(), seat.getStatus()),
                this.scheduleService.convertFetchScheduleDTO(schedule));

        return res;
    }

}
