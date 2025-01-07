package com.datmai.moviereservation.domain;

import java.time.Instant;
import java.util.List;

import com.datmai.moviereservation.common.constant.MovieAge;
import com.datmai.moviereservation.common.constant.MovieGenre;
import com.datmai.moviereservation.common.security.SecurityUtil;

import jakarta.persistence.Column;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Movie name cannot be empty")
    private String name;

    private String poster;

    @Enumerated(EnumType.STRING)
    private MovieGenre genre;

    @NotNull(message = "Duration cannot be empty")
    private int duration;

    @NotBlank(message = "Country cannot be blank")
    private String country;
    private String subtitle;

    @Enumerated(EnumType.STRING)
    private MovieAge age;

    @NotBlank(message = "Country cannot be blank")
    private String director;

    @Column(columnDefinition = "TEXT")
    private String description;

    private List<String> actors;

    // One To Many -> Schedule
    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }

}
