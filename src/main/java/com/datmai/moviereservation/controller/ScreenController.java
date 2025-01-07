package com.datmai.moviereservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.service.ScreenService;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.screen.ScreenDTO;
import com.datmai.moviereservation.exception.ExistingException;
import com.datmai.moviereservation.common.format.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Screen Controller")
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping("/screens")
    @ApiMessage("Create screen successfully")
    public ResponseEntity<ScreenDTO> createScreen(@RequestBody Screen screen) throws ExistingException {
        // Check if screen exist
        if (this.screenService.isScreenExist(screen.getName())) {
            throw new ExistingException(
                    List.of("Screen " + screen.getName() + " already exists"));
        }

        ScreenDTO newScreen = this.screenService.createScreen(screen);
        return ResponseEntity.ok().body(newScreen);
    }

    @PutMapping("/screens")
    @ApiMessage("Update screen successfully")
    public ResponseEntity<ScreenDTO> updateScreen(@RequestBody Screen screen) throws ExistingException {

        //Validate update screen request
        this.screenService.validateUpdateScreenRequest(screen);

        ScreenDTO updatedScreen = this.screenService.updateScreen(screen);

        return ResponseEntity.ok().body(updatedScreen);
    }

    @GetMapping("/screens/{id}")
    @ApiMessage("Fetch screen successfully")
    public ResponseEntity<ScreenDTO> fetchScreen(@PathVariable long id) throws ExistingException {
        // Check if screen id exist
        Screen screen = this.screenService.fetchScreenById(id);
        if (screen == null) {
            throw new ExistingException(
                    List.of("Screen id = " + id + " does not exist"));
        }

        ScreenDTO res = this.screenService.convertScreenDTO(screen);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/screens")
    @ApiMessage("Fetch screens successfully")
    public ResponseEntity<ResultPaginationDTO> fetchScreens(
        @Filter Specification<Screen> specification,
        Pageable pageable
    ) throws ExistingException {
        ResultPaginationDTO res = this.screenService.fetchAllScreens(specification, pageable);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/screens/{id}")
    @ApiMessage("Delete screen successfully")
    public ResponseEntity<Void> deleteScreen(@PathVariable long id) throws ExistingException {
        // Check if screen id exist
        Screen screen = this.screenService.fetchScreenById(id);
        if (screen == null) {
            throw new ExistingException(
                    List.of("Screen id = " + id + " does not exist"));
        }

        this.screenService.deleteScreen(screen);
        return ResponseEntity.ok(null);
    }
}
