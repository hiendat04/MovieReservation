package com.datmai.moviereservation.domain;

import java.util.List;

import com.datmai.moviereservation.common.constant.MovieAge;
import com.datmai.moviereservation.common.constant.MovieGenre;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie extends AbstractEntity<Long>  {

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


}
