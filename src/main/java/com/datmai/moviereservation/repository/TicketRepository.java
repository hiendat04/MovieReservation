package com.datmai.moviereservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Seat;
import com.datmai.moviereservation.domain.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket>{
    boolean existsByScheduleAndSeat(Schedule schedule, Seat seat);
}
