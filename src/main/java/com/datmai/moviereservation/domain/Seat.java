package com.datmai.moviereservation.domain;

import org.hibernate.validator.constraints.Range;

import com.datmai.moviereservation.util.constant.SeatRow;
import com.datmai.moviereservation.util.constant.SeatStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seats")
public class Seat {
    private static final int MIN_RANGE = 1;

    private static final int MAX_RANGE = 10;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_row")
    private SeatRow row;

    @Range(min = MIN_RANGE, max = MAX_RANGE)
    private int number;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    // Many To One -> Screen
    @ManyToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;

    // One To One -> Ticket
    @OneToOne(mappedBy = "seat", fetch = FetchType.LAZY)
    private Ticket ticket;
}
