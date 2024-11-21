package com.datmai.moviereservation.domain;

import java.time.Instant;
import java.util.List;

import com.datmai.moviereservation.util.constant.MovieAge;
import com.datmai.moviereservation.util.constant.MovieGenre;
import com.datmai.moviereservation.util.security.SecurityUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Movie name cannot be empty")
    private String name;

    @NotBlank(message = "Movie genre cannot be blank")
    @Enumerated(EnumType.STRING)
    private MovieGenre genre;

    @NotBlank(message = "Duration cannot be blank")
    private int duration;

    @NotBlank(message = "Country cannot be blank")
    private String country;
    private String subtitle;

    @Enumerated(EnumType.STRING)
    private MovieAge age;

    @NotBlank(message = "Country cannot be blank")
    private String director;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private List<String> actors;

    // One To Many -> Schedule
    @OneToMany(mappedBy = "movie")
    private List<Schedule> schedules;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }

}
