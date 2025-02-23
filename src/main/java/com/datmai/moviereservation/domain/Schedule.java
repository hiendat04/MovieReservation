package com.datmai.moviereservation.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.datmai.moviereservation.common.security.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "schedules")
public class Schedule extends AbstractEntity<Long> {

    @NotNull(message = "Show time cannot be empty")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    // Many To One -> Screen
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    // Many To One > Movie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // One To Many -> Ticket
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    // One To Many -> ShowTime
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    private List<ShowTime> showTimes;
}
