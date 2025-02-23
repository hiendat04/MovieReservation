package com.datmai.moviereservation.domain;

import java.time.Instant;
import java.util.List;

import com.datmai.moviereservation.common.constant.ScreenFormat;
import com.datmai.moviereservation.common.constant.ScreenName;
import com.datmai.moviereservation.common.security.SecurityUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "screens")
public class Screen extends AbstractEntity<Long> {

    @Enumerated(EnumType.STRING)
    private ScreenName name;

    @Enumerated(EnumType.STRING)
    private ScreenFormat format;

    // One To Many -> Schedule
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    // One To Many -> Seat
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    private List<Seat> seats;
}
