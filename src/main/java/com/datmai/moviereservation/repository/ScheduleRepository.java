package com.datmai.moviereservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.datmai.moviereservation.domain.Movie;
import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Screen;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, JpaSpecificationExecutor<Schedule> {
    
    boolean existsByScreenAndMovieAndDate(Screen screen, Movie movie, LocalDate date);
    List<Schedule> findByScreenId(long id);
}
