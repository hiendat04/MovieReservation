package com.datmai.moviereservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.Ticket;
import com.datmai.moviereservation.service.SeatService;
import com.datmai.moviereservation.service.TicketService;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.ticket.TicketDTO;
import com.datmai.moviereservation.exception.ExistingException;
import com.datmai.moviereservation.common.format.ApiMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Ticket Controller")
public class TicketController {
    private final TicketService ticketService;
    private final SeatService seatService;

    @PostMapping("/tickets")
    @ApiMessage("Create ticket successfully")
    public ResponseEntity<TicketDTO> createTicket(@RequestBody Ticket ticket) throws ExistingException {

        // Check if ticket exists by movie and seat
        if (this.ticketService.isTicketExist(ticket.getSchedule(), ticket.getSeat())) {
            throw new ExistingException(
                    List.of("Ticket already exists"));
        }

        // Create new ticket
        TicketDTO newTicket = this.ticketService.createTicket(ticket);

        return ResponseEntity.status(HttpStatus.CREATED).body(newTicket);
    }

    @PutMapping("/tickets")
    @ApiMessage("Update schedule and seat successfully")
    public ResponseEntity<TicketDTO> updateTicket(@RequestBody Ticket ticket) throws Exception {

        //Validate update ticket request
        this.ticketService.validateUpdateTicketRequest(ticket);

        TicketDTO updatedTicket = this.ticketService.updateTicket(ticket);

        return ResponseEntity.ok().body(updatedTicket);
    }

    @GetMapping("tickets/{id}")
    @ApiMessage("Fetch ticket successfully")
    public ResponseEntity<TicketDTO> fetchTicket(@PathVariable long id) throws ExistingException {
        // Check if ticket id exist
        if (!this.ticketService.isTicketIdExist(id)) {
            throw new ExistingException(
                    List.of("Ticket id = " + id + " does not exist"));
        }
        Ticket ticket = this.ticketService.fetchTicketById(id);
        TicketDTO ticketDTO = this.ticketService.convertTicketDTO(ticket);
        return ResponseEntity.ok().body(ticketDTO);
    }

    @GetMapping("tickets")
    @ApiMessage("Fetch all tickets successfully")
    public ResponseEntity<ResultPaginationDTO> fetchAllTickets(Pageable pageable, Specification<Ticket> specification) {
        ResultPaginationDTO allTickets = this.ticketService.fetchAllTickets(specification, pageable);
        return ResponseEntity.ok().body(allTickets);
    }

    @DeleteMapping("tickets/{id}")
    @ApiMessage("Delete ticket successfully")
    public ResponseEntity<Void> deleteTicket(@PathVariable long id) throws ExistingException {
        // Check if ticket id exist
        if (!this.ticketService.isTicketIdExist(id)) {
            throw new ExistingException(
                    List.of("Ticket id = " + id + " does not exist"));
        }

        // Delete ticket
        this.ticketService.deleteTicket(id);
        return ResponseEntity.ok().body(null);
    }

}
