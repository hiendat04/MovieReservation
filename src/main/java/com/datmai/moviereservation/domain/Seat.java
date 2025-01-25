package com.datmai.moviereservation.domain;

import java.time.Instant;

import com.datmai.moviereservation.common.constant.SeatStatus;
import com.datmai.moviereservation.common.security.SecurityUtil;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seats")
public class Seat extends AbstractEntity<Long> {
    @Column(name = "seat_identifier", unique = true, nullable = false)
    private String seatIdentifier;

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
