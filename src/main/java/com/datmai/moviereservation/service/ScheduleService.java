package com.datmai.moviereservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.datmai.moviereservation.exception.ExistingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Movie;
import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.repository.ScheduleRepository;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.schedule.CreateScheduleDTO;
import com.datmai.moviereservation.common.dto.response.schedule.FetchScheduleDTO;
import com.datmai.moviereservation.common.dto.response.schedule.UpdateScheduleDTO;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MovieService movieService;
    private final ScreenService screenService;


    public boolean isScheduleExist(Screen screen, Movie movie, LocalDate date) {
        return this.scheduleRepository.existsByScreenAndMovieAndDate(screen, movie, date);
    }

    public CreateScheduleDTO createSchedule(Schedule schedule) {
        Schedule savedSchedule = this.scheduleRepository.save(schedule);
        return this.convertCreateScheduleDTO(savedSchedule);
    }

    public void validateCreateScheduleRequest(Schedule schedule) {
        List<String> errors = new ArrayList<>();
        // Check if movie exist
        if (this.movieService.fetchMovieById(schedule.getMovie().getId()) == null) {
            errors.add("Movie id = " + schedule.getMovie().getId() + " does not exist");
        }

        // Check if screen exist
        if (this.screenService.fetchScreenById(schedule.getScreen().getId()) == null) {
            errors.add("Screen id = " + schedule.getScreen().getId() + " does not exist");
        }

        // Check if schedule exist
        if (this.isScheduleExist(schedule.getScreen(), schedule.getMovie(), schedule.getDate())) {
            errors.add("Schedule already exist");
        }

        if(!errors.isEmpty()) {
            throw new ExistingException(errors);
        }
    }

    public void validateUpdateScheduleRequest(Schedule schedule) {
        List<String> errors = new ArrayList<>();
        // Check if id exist
        if (this.fetchScheduleById(schedule.getId()) == null) {
            errors.add("Schedule id = " + schedule.getId() + " does not exist");
        }

        // Check if schedule exist
        if (this.isScheduleExist(schedule.getScreen(), schedule.getMovie(), schedule.getDate())) {
            errors.add("Schedule already exist");
        }

        if(!errors.isEmpty()) {
            throw new ExistingException(errors);
        }
    }

    public CreateScheduleDTO convertCreateScheduleDTO(Schedule schedule) {
        Movie movie = this.movieService.fetchMovieById(schedule.getMovie().getId());
        Screen screen = this.screenService.fetchScreenById(schedule.getScreen().getId());

        CreateScheduleDTO res = new CreateScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getCreatedAt(),
                schedule.getCreatedBy(),
                new CreateScheduleDTO.ScheduleScreen(schedule.getScreen().getId(), screen.getName()),
                new CreateScheduleDTO.ScheduleMovie(schedule.getMovie().getId(), movie.getName()));

        return res;
    }

    public Schedule fetchScheduleById(long id) {
        Optional<Schedule> scheduleOptional = this.scheduleRepository.findById(id);
        Schedule currentSchedule = scheduleOptional.orElse(null);
        return currentSchedule;
    }

    public UpdateScheduleDTO updateSchedule(Schedule schedule) {
        Schedule currentSchedule = this.fetchScheduleById(schedule.getId());
        if (currentSchedule != null) {
            currentSchedule.setDate(schedule.getDate());
            currentSchedule.setMovie(schedule.getMovie());
            currentSchedule.setScreen(schedule.getScreen());

            currentSchedule = this.scheduleRepository.save(currentSchedule);
        }
        return this.convertUpdateScheduleDTO(currentSchedule);
    }

    public UpdateScheduleDTO convertUpdateScheduleDTO(Schedule schedule) {
        if (schedule == null) {return null;}
        Movie movie = this.movieService.fetchMovieById(schedule.getMovie().getId());
        Screen screen = this.screenService.fetchScreenById(schedule.getScreen().getId());
        UpdateScheduleDTO res = new UpdateScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getUpdatedAt(),
                schedule.getUpdatedBy(),
                new UpdateScheduleDTO.ScheduleScreen(schedule.getScreen().getId(), screen.getName()),
                new UpdateScheduleDTO.ScheduleMovie(schedule.getMovie().getId(), movie.getName()));

        return res;
    }

    public FetchScheduleDTO convertFetchScheduleDTO(Schedule schedule) {
        FetchScheduleDTO res = new FetchScheduleDTO(
                schedule.getDate(),
                new FetchScheduleDTO.ScheduleMovie(schedule.getMovie().getId(), schedule.getMovie().getName(),
                        schedule.getMovie().getPoster(), schedule.getMovie().getGenre()),
                new FetchScheduleDTO.ScheduleScreen(schedule.getScreen().getId(), schedule.getScreen().getName()));
        return res;
    }

    public ResultPaginationDTO fetchAllSchedules(Specification<Schedule> spec, Pageable pageable) {
        Page<Schedule> pageSchedule = this.scheduleRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();

        List<FetchScheduleDTO> results = pageSchedule.getContent().stream().map(
                schedule -> {
                    FetchScheduleDTO result = new FetchScheduleDTO(
                            schedule.getDate(),
                            new FetchScheduleDTO.ScheduleMovie(schedule.getMovie().getId(),
                                    schedule.getMovie().getName(), schedule.getMovie().getPoster(),
                                    schedule.getMovie().getGenre()),
                            new FetchScheduleDTO.ScheduleScreen(schedule.getScreen().getId(),
                                    schedule.getScreen().getName()));
                    return result;
                }).toList();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                pageSchedule.getNumber(),
                pageSchedule.getSize(),
                pageSchedule.getTotalPages(),
                pageSchedule.getNumberOfElements());

        res.setMeta(meta);
        res.setResult(results);
        return res;
    }

    public void deleteById(long id) {
        this.scheduleRepository.deleteById(id);
    }
}
