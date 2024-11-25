package com.datmai.moviereservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.service.ScreenService;
import com.datmai.moviereservation.util.dto.response.schedule.CreateScheduleDTO;
import com.datmai.moviereservation.util.error.ExistingException;
import com.datmai.moviereservation.util.format.ApiMessage;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @PostMapping("/screens")
    @ApiMessage("Create screen successfully")
    public ResponseEntity<Screen> createScreen(@RequestBody Screen screen) throws ExistingException {
        // Check if screen exist
        if (this.screenService.isScreenExist(screen.getName())) {
            throw new ExistingException("Screen " + screen.getName() + " already exists");
        }

        Screen newScreen = this.screenService.createScreen(screen);
        return ResponseEntity.ok().body(newScreen);
    }

}
