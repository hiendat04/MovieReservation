package com.datmai.moviereservation.service;

import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Seat;
import com.datmai.moviereservation.repository.SeatRepository;

@Service
public class SeatService {
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public Seat fetchSeatById(long id) {
        return this.seatRepository.findById(id) != null
                ? this.seatRepository.findById(id).get()
                : null;
    }
}
