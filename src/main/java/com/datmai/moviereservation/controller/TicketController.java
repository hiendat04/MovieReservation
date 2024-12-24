package com.datmai.moviereservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.Ticket;
import com.datmai.moviereservation.service.TicketService;
import com.datmai.moviereservation.util.dto.response.ticket.TicketDTO;
import com.datmai.moviereservation.util.error.ExistingException;
import com.datmai.moviereservation.util.format.ApiMessage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(
            TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/tickets")
    @ApiMessage("Create ticket successfully")
    public ResponseEntity<TicketDTO> createTicket(@RequestBody Ticket ticket) throws ExistingException {
        
        // Check if ticket exists by movie, schedule and seat
        if (this.ticketService.isTicketExist(ticket.getSchedule(), ticket.getSeat())) {
            throw new ExistingException("Ticket already exists");
        }

        // Create new ticket
        TicketDTO newTicket = this.ticketService.createTicket(ticket);

        return ResponseEntity.status(HttpStatus.CREATED).body(newTicket);
    }
    

}
