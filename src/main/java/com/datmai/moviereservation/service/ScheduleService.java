package com.datmai.moviereservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Movie;
import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.repository.ScheduleRepository;
import com.datmai.moviereservation.util.constant.ScreenFormat;
import com.datmai.moviereservation.util.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.util.dto.response.schedule.CreateScheduleDTO;
import com.datmai.moviereservation.util.dto.response.schedule.FetchScheduleDTO;
import com.datmai.moviereservation.util.dto.response.schedule.UpdateScheduleDTO;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MovieService movieService;
    private final ScreenService screenService;

    public ScheduleService(ScheduleRepository scheduleRepository, MovieService movieService,
            ScreenService screenService) {
        this.scheduleRepository = scheduleRepository;
        this.movieService = movieService;
        this.screenService = screenService;
    }

    public boolean isScheduleExist(Screen screen, Movie movie, LocalDate date, ScreenFormat format) {
        return this.scheduleRepository.existsByScreenAndMovieAndDateAndFormat(screen, movie, date, format);
    }

    public Schedule createSchedule(Schedule schedule) {
        if (!this.isScheduleExist(schedule.getScreen(), schedule.getMovie(), schedule.getDate(),
                schedule.getFormat())) {
            return this.scheduleRepository.save(schedule);
        }
        return null;
    }

    public CreateScheduleDTO convertCreateScheduleDTO(Schedule schedule) {
        Movie movie = this.movieService.fetchMovieById(schedule.getMovie().getId());
        Screen screen = this.screenService.fetchScreenById(schedule.getScreen().getId());

        CreateScheduleDTO res = new CreateScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getFormat(),
                schedule.getCreatedAt(),
                schedule.getCreatedBy(),
                new CreateScheduleDTO.ScheduleScreen(schedule.getScreen().getId(), screen.getName()),
                new CreateScheduleDTO.ScheduleMovie(schedule.getMovie().getId(), movie.getName()));

        return res;
    }

    public Schedule fetchScheduleById(long id) {
        Optional<Schedule> scheduleOptional = this.scheduleRepository.findById(id);
        Schedule currentSchedule = scheduleOptional.isPresent() ? scheduleOptional.get() : null;
        return currentSchedule;
    }

    public Schedule updateSchedule(Schedule schedule) {
        Schedule currentSchedule = this.fetchScheduleById(schedule.getId());
        if (currentSchedule != null) {
            currentSchedule.setDate(schedule.getDate());
            currentSchedule.setFormat(schedule.getFormat());
            currentSchedule.setMovie(schedule.getMovie());
            currentSchedule.setScreen(schedule.getScreen());

            currentSchedule = this.scheduleRepository.save(currentSchedule);
        }
        return currentSchedule;
    }

    public UpdateScheduleDTO convertUpdateDTO(Schedule schedule) {
        Movie movie = this.movieService.fetchMovieById(schedule.getMovie().getId());
        Screen screen = this.screenService.fetchScreenById(schedule.getScreen().getId());
        UpdateScheduleDTO res = new UpdateScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getFormat(),
                schedule.getUpdatedAt(),
                schedule.getUpdatedBy(),
                new UpdateScheduleDTO.ScheduleScreen(schedule.getScreen().getId(), screen.getName()),
                new UpdateScheduleDTO.ScheduleMovie(schedule.getMovie().getId(), movie.getName()));

        return res;
    }

    public FetchScheduleDTO convertFetchScheduleDTO(Schedule schedule) {
        FetchScheduleDTO res = new FetchScheduleDTO(
                schedule.getDate(),
                schedule.getFormat(),
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
                            schedule.getFormat(),
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

    public void deleteById(long id){
        this.scheduleRepository.deleteById(id);
    }
}
