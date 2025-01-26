package com.datmai.moviereservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.ShowTime;
import com.datmai.moviereservation.service.ScheduleService;
import com.datmai.moviereservation.service.ShowTimeService;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.showtime.ShowTimeDTO;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Show Time Controller")
public class ShowTimeController {

    private final ShowTimeService showTimeService;
    private final ScheduleService scheduleService;

    @PostMapping("/show-times")
    @ApiMessage("Create show time successfully")
    public ResponseEntity<ShowTimeDTO> createShowTime(@Valid @RequestBody ShowTime time) throws ExistingException {

        // Validate create show time request
        this.showTimeService.validateCreateShowTimeReq(time);

        ShowTimeDTO showTime = this.showTimeService.createShowTime(time);

        return ResponseEntity.status(HttpStatus.CREATED).body(showTime);
    }

    @PutMapping("/show-times")
    @ApiMessage("Update show time successfully")
    public ResponseEntity<ShowTimeDTO> updateShowTime(@RequestBody ShowTime showTime) throws ExistingException {

        //Validate update show time request
        this.showTimeService.validateUpdateShowTimeReq(showTime);

        // Update show time
        ShowTimeDTO res = this.showTimeService.updateShowTime(showTime);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/show-times/{id}")
    @ApiMessage("Fetch show time successfully")
    public ResponseEntity<ShowTimeDTO> fetchShowTime(@PathVariable long id) throws ExistingException {

        // Check if show time exists
        this.showTimeService.validateShowTimeExist(id);

        ShowTime fetchedShowTime = this.showTimeService.fetchShowTimeById(id);
        ShowTimeDTO res = this.showTimeService.convertShowTimeDTO(fetchedShowTime);
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
        this.showTimeService.validateShowTimeExist(id);

        // Delete show time
        this.showTimeService.deleteShowTime(id);
        return ResponseEntity.ok(null);
    }

}
