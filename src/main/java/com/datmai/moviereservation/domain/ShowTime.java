package com.datmai.moviereservation.domain;

import java.time.Instant;
import java.time.LocalTime;

import com.datmai.moviereservation.common.security.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "show_times")
public class ShowTime extends AbstractEntity<Long> {

    @NotNull(message = "Show time cannot be empty")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time; // Only the time

    //Many To One -> Schedule
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
}
