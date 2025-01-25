package com.datmai.moviereservation.domain;

import java.time.Instant;

import com.datmai.moviereservation.common.security.SecurityUtil;

import jakarta.persistence.Entity;
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
@Table(name = "tickets")
public class Ticket extends AbstractEntity<Long>{

    private Double price;

    // One To One -> Seat
    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    // Many To One -> Schedule
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // Many To One -> User
    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id")
    private User user;
}
