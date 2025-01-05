package com.datmai.moviereservation.common.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.datmai.moviereservation.domain.Seat;
import com.datmai.moviereservation.repository.SeatRepository;
import com.datmai.moviereservation.common.constant.SeatStatus;

import java.util.ArrayList;
import java.util.List;

@Component
public class SeatSeeder implements CommandLineRunner {

    private final SeatRepository seatRepository;

    public SeatSeeder(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Clear existing data (optional)
        if (seatRepository.count() == 0) {
            // Seed data
            List<Seat> seats = new ArrayList<>();
            String[] rows = { "A", "B", "C", "D" };
            int seatsPerRow = 10;

            for (String row : rows) {
                for (int number = 1; number <= seatsPerRow; number++) {
                    Seat seat = new Seat();
                    seat.setSeatIdentifier(row + number);
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seats.add(seat);
                }
            }

            // Save all seats
            seatRepository.saveAll(seats);
            System.out.println("Seating data initialized successfully!");
        }

    }
}