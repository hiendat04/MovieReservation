package com.datmai.moviereservation.service;

import com.datmai.moviereservation.exception.ExistingException;
import com.datmai.moviereservation.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Seat;
import com.datmai.moviereservation.domain.Ticket;
import com.datmai.moviereservation.repository.TicketRepository;
import com.datmai.moviereservation.common.constant.SeatStatus;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.ticket.TicketDTO;

import java.util.ArrayList;
import java.util.List;

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

    public boolean isTicketIdExist(long id) {
        return this.ticketRepository.existsById(id);
    }

    public Ticket fetchTicketById(long id) {
        return this.ticketRepository.findById(id).isPresent() ? this.ticketRepository.findById(id).get() : null;
    }

    public void validateUpdateTicketRequest(Ticket ticket) {
        List<String> errors = new ArrayList<>();
        // Check if ticket id exist
        if (!this.isTicketIdExist(ticket.getId())) {
            errors.add("Ticket id = " + ticket.getId() + " does not exist");
        }

        // Check if ticket exists by movie and seat
        if (this.isTicketExist(ticket.getSchedule(), ticket.getSeat())) {
            errors.add("Ticket already exists");
        }

        // Only allow update ticket if its seat is AVAILABLE
        if (this.seatService.fetchSeatById(ticket.getSeat().getId()).getStatus() != SeatStatus.AVAILABLE) {
            errors.add("Seat is not available. Please choose another seat");
        }

        if(!errors.isEmpty()) {
            throw new ExistingException(errors);
        }
    }

    public TicketDTO updateTicket(Ticket ticket) {
        Ticket currentTicket = this.fetchTicketById(ticket.getId());
        if (currentTicket != null) {
            currentTicket.setSchedule(ticket.getSchedule());
            currentTicket.setSeat(ticket.getSeat());
            currentTicket.setUser(ticket.getUser());

            // Save updated ticket
            currentTicket = this.ticketRepository.save(currentTicket);
        }
        return this.convertTicketDTO(currentTicket);
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

    public ResultPaginationDTO fetchAllTickets(Specification<Ticket> specification, Pageable pageable) {
        Page<Ticket> ticketPage = this.ticketRepository.findAll(specification, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalPages(),
                ticketPage.getTotalElements());

        ResultPaginationDTO allTickets = new ResultPaginationDTO(
                meta,
                ticketPage.getContent().stream().map(ticket -> {
                    return this.convertTicketDTO(ticket);
                }));

        return allTickets;
    }

    public void deleteTicket(long id) {
        this.ticketRepository.deleteById(id);
    }

}
