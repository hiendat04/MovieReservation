package com.datmai.moviereservation.domain;

import java.time.Instant;
import java.util.List;

import com.datmai.moviereservation.util.constant.ScreenFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Show time cannot be empty")
    private Instant showTime;

    @Enumerated(EnumType.STRING)
    private ScreenFormat format;

    // Many To One -> Screen
    @ManyToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;

    // Many To One > Movie
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // One To Many -> Ticket
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    private List<Ticket> tickets;
}
