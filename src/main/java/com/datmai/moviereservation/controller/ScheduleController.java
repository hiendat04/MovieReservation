package com.datmai.moviereservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.service.MovieService;
import com.datmai.moviereservation.service.ScheduleService;
import com.datmai.moviereservation.service.ScreenService;
import com.datmai.moviereservation.util.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.util.dto.response.schedule.CreateScheduleDTO;
import com.datmai.moviereservation.util.dto.response.schedule.FetchScheduleDTO;
import com.datmai.moviereservation.util.dto.response.schedule.UpdateScheduleDTO;
import com.datmai.moviereservation.util.error.ExistingException;
import com.datmai.moviereservation.util.format.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final MovieService movieService;
    private final ScreenService screenService;

    public ScheduleController(
            ScheduleService scheduleService,
            MovieService movieService,
            ScreenService screenService) {
        this.scheduleService = scheduleService;
        this.movieService = movieService;
        this.screenService = screenService;
    }

    @PostMapping("/schedules")
    public ResponseEntity<CreateScheduleDTO> createSchedule(@Valid @RequestBody Schedule schedule)
            throws ExistingException {

        // Check if movie exist
        if (this.movieService.fetchMovieById(schedule.getMovie().getId()) == null) {
            throw new ExistingException("Movie id = " + schedule.getMovie().getId() + " does not exist");
        }

        // Check if screen exist
        if (this.screenService.fetchScreenById(schedule.getScreen().getId()) == null) {
            throw new ExistingException("Screen id = " + schedule.getScreen().getId() + " does not exist");
        }

        // Check if schedule exist
        if (this.scheduleService.isScheduleExist(schedule.getScreen(), schedule.getMovie(), schedule.getDate())) {
            throw new ExistingException("Schedule already exist");
        }

        // Create new schedule
        Schedule newSchedule = this.scheduleService.createSchedule(schedule);

        // Convert DTO
        CreateScheduleDTO res = this.scheduleService.convertCreateScheduleDTO(newSchedule);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/schedules")
    @ApiMessage("Update schedule successfully")
    public ResponseEntity<UpdateScheduleDTO> updateSchedule(@RequestBody Schedule schedule) throws ExistingException {
        // Check if id exist
        if (this.scheduleService.fetchScheduleById(schedule.getId()) == null) {
            throw new ExistingException("Schedule id = " + schedule.getId() + " does not exist");
        }

        // Check if schedule exist
        if (this.scheduleService.isScheduleExist(schedule.getScreen(), schedule.getMovie(), schedule.getDate())) {
            throw new ExistingException("Schedule already exist");
        }

        // Update schedule
        Schedule updatedSchedule = this.scheduleService.updateSchedule(schedule);
        UpdateScheduleDTO res = this.scheduleService.convertUpdateDTO(updatedSchedule);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/schedules/{id}")
    @ApiMessage("Fetch schedule successfully")
    public ResponseEntity<FetchScheduleDTO> fetchASchedule(@PathVariable long id) throws ExistingException {

        // Check if id exist
        Schedule schedule = this.scheduleService.fetchScheduleById(id);
        if (schedule == null) {
            throw new ExistingException("Schedule id = " + id + " does not exist");
        }
        FetchScheduleDTO res = this.scheduleService.convertFetchScheduleDTO(schedule);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/schedules")
    @ApiMessage("Fetch all schedules successfully")
    public ResponseEntity<ResultPaginationDTO> fetchSchedules(@Filter Specification<Schedule> spec, Pageable pageable) {
        ResultPaginationDTO res = this.scheduleService.fetchAllSchedules(spec, pageable);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/schedules/{id}")
    @ApiMessage("Delete schedule successfully")
    public ResponseEntity<Void> deleteSchedule(@PathVariable long id) throws ExistingException {
        // Check if id exist
        Schedule schedule = this.scheduleService.fetchScheduleById(id);
        if (schedule == null) {
            throw new ExistingException("Schedule id = " + id + " does not exist");
        }
        this.scheduleService.deleteById(id);
        return ResponseEntity.ok(null);
    }

}
