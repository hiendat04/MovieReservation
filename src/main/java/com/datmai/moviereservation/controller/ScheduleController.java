package com.datmai.moviereservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.service.MovieService;
import com.datmai.moviereservation.service.ScheduleService;
import com.datmai.moviereservation.service.ScreenService;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.schedule.CreateScheduleDTO;
import com.datmai.moviereservation.common.dto.response.schedule.FetchScheduleDTO;
import com.datmai.moviereservation.common.dto.response.schedule.UpdateScheduleDTO;
import com.datmai.moviereservation.exception.ExistingException;
import com.datmai.moviereservation.common.format.ApiMessage;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Schedule Controller")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final MovieService movieService;
    private final ScreenService screenService;

    @PostMapping("/schedules")
    public ResponseEntity<CreateScheduleDTO> createSchedule(@Valid @RequestBody Schedule schedule)
            throws ExistingException {

       //Validate create schedule request
        this.scheduleService.validateCreateScheduleRequest(schedule);

        // Create new schedule
        CreateScheduleDTO newSchedule = this.scheduleService.createSchedule(schedule);

        return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
    }

    @PutMapping("/schedules")
    @ApiMessage("Update schedule successfully")
    public ResponseEntity<UpdateScheduleDTO> updateSchedule(@RequestBody Schedule schedule) throws ExistingException {
        // Validate update schedule request
        this.scheduleService.validateUpdateScheduleRequest(schedule);

        // Update schedule
        UpdateScheduleDTO updatedSchedule = this.scheduleService.updateSchedule(schedule);

        return ResponseEntity.ok().body(updatedSchedule);
    }

    @GetMapping("/schedules/{id}")
    @ApiMessage("Fetch schedule successfully")
    public ResponseEntity<FetchScheduleDTO> fetchASchedule(@PathVariable long id) throws ExistingException {

        // Check if id exist
        Schedule schedule = this.scheduleService.fetchScheduleById(id);
        if (schedule == null) {
            throw new ExistingException(
                    List.of("Schedule id = " + id + " does not exist"));
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
            throw new ExistingException(
                    List.of("Schedule id = " + id + " does not exist"));
        }
        this.scheduleService.deleteById(id);
        return ResponseEntity.ok(null);
    }

}
