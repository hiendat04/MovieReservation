package com.datmai.moviereservation.domain;

import java.time.Instant;

import com.datmai.moviereservation.util.security.SecurityUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Price cannot be blank")
    private double price;

    private Instant boughtAt;
    private String boughtBy;

    // One To One -> Seat
    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    // Many To One -> Movie
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // Many To One -> Schedule
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // Many To One -> User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void handleBeforeCreate() {
        this.boughtBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.boughtAt = Instant.now();
    }
}
