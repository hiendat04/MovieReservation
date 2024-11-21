package com.datmai.moviereservation.domain;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "screens")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    // One To Many -> Schedule
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    // One To Many -> Seat
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    private List<Seat> seats;
    
}
