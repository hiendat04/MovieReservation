package com.datmai.moviereservation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.ShowTime;
import com.datmai.moviereservation.service.ScheduleService;
import com.datmai.moviereservation.service.ShowTimeService;
import com.datmai.moviereservation.util.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.util.dto.response.showtime.ShowTimeDTO;
import com.datmai.moviereservation.util.error.ExistingException;
import com.datmai.moviereservation.util.format.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class ShowTimeController {

    private final ShowTimeService showTimeService;
    private final ScheduleService scheduleService;

    public ShowTimeController(ShowTimeService showTimeService, ScheduleService scheduleService) {
        this.showTimeService = showTimeService;
        this.scheduleService = scheduleService;
    }

    @PostMapping("/show-times")
    @ApiMessage("Create show time successfully")
    public ResponseEntity<ShowTimeDTO> createShowTime(@Valid @RequestBody ShowTime time) throws ExistingException {

        // Check if schedule exists
        if (this.scheduleService.fetchScheduleById(time.getSchedule().getId()) == null) {
            throw new ExistingException("Schedule id = " + time.getSchedule().getId() + " does not exist");
        }

        // Then check if show time exists in that schedule
        if (this.showTimeService.isShowTimeExist(time.getTime(), time.getSchedule())) {
            throw new ExistingException(
                    "Show time at " + time.getTime() + " on "
                            + this.scheduleService.fetchScheduleById(time.getSchedule().getId()).getDate()
                            + " already exists");
        }

        // Finally check if new show time overlap
        this.showTimeService.isShowTimeOverlap(time.getTime(), time.getSchedule().getId(), null);

        ShowTime showTime = this.showTimeService.createShowTime(time);
        ShowTimeDTO res = this.showTimeService.convertShowTimeDTO(showTime);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/show-times")
    @ApiMessage("Update show time successfully")
    public ResponseEntity<ShowTimeDTO> updateShowTime(@RequestBody ShowTime showTime) throws ExistingException {
        // Check if show time exist by id
        if (this.showTimeService.fetchShowTimeById(showTime.getId()) == null) {
            throw new ExistingException("Show time id = " + showTime.getId() + " does not exist");
        }

        // Check if request update show time already exists
        if (this.showTimeService.isShowTimeExist(showTime.getTime(), showTime.getSchedule())) {
            throw new ExistingException(
                    "Show time at " + showTime.getTime() + " on "
                            + this.scheduleService.fetchScheduleById(showTime.getSchedule().getId()).getDate()
                            + " already exists");
        }

        // Check if the updated show time will overlap
        long scheduleId = this.showTimeService.fetchShowTimeById(showTime.getId()).getSchedule().getId();
        this.showTimeService.isShowTimeOverlap(showTime.getTime(), scheduleId, showTime.getId());

        // Update show time
        ShowTimeDTO res = this.showTimeService.updateShowTime(showTime);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/show-times/{id}")
    @ApiMessage("Fetch show time successfully")
    public ResponseEntity<ShowTimeDTO> fetchShowTime(@PathVariable long id) throws ExistingException {

        // Check if show time exists
        ShowTime showTime = this.showTimeService.fetchShowTimeById(id);
        if (showTime == null) {
            throw new ExistingException("Show time id = " + id + " does not exist");
        }
        ShowTimeDTO res = this.showTimeService.convertShowTimeDTO(showTime);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/show-times")
    @ApiMessage("Fetch all show time successfully")
    public ResponseEntity<ResultPaginationDTO> fetchAllShowTimes(
            @Filter Specification<ShowTime> spec, Pageable pageable) {
        ResultPaginationDTO res = this.showTimeService.fetchAllShowTimes(spec, pageable);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/show-times/{id}")
    @ApiMessage("Delete show time successfully")
    public ResponseEntity<Void> deleteShowTime(@PathVariable long id) throws ExistingException {
        // Check if show time exists
        ShowTime showTime = this.showTimeService.fetchShowTimeById(id);
        if (showTime == null) {
            throw new ExistingException("Show time id = " + id + " does not exist");
        }

        this.showTimeService.deleteShowTime(showTime);
        return ResponseEntity.ok(null);
    }

}
