package com.datmai.moviereservation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.datmai.moviereservation.exception.ExistingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.repository.ScheduleRepository;
import com.datmai.moviereservation.repository.ScreenRepository;
import com.datmai.moviereservation.common.constant.ScreenName;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.schedule.CreateScheduleDTO;
import com.datmai.moviereservation.common.dto.response.screen.ScreenDTO;

@Service
public class ScreenService {
    private final ScreenRepository screenRepository;
    private final ScheduleRepository scheduleRepository;

    public ScreenService(ScreenRepository screenRepository, ScheduleRepository scheduleRepository) {
        this.screenRepository = screenRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public Screen fetchScreenById(long id) {
        Optional<Screen> screenOptional = this.screenRepository.findById(id);
        Screen currentScreen = screenOptional.orElse(null);
        return currentScreen;
    }

    public boolean isScreenExist(ScreenName name) {
        return this.screenRepository.existsByName(name);
    }

    public ScreenDTO createScreen(Screen screen) {
        if (!this.isScreenExist(screen.getName())) {
            return this.convertScreenDTO(this.screenRepository.save(screen));
        }
        return null;
    }

    public void validateUpdateScreenRequest(Screen screen) {
        List<String> errors = new ArrayList<>();
        // Check if screen id exist
        if (this.fetchScreenById(screen.getId()) == null) {
            errors.add("Screen id = " + screen.getId() + " does not exist");
        }
        // Check if screen name exists
        if (this.isScreenExist(screen.getName())) {
            errors.add("Screen " + screen.getName() + " already exists");
        }

        if(!errors.isEmpty()) {
            throw new ExistingException(errors);
        }

    }

    public ScreenDTO updateScreen(Screen screen) {
        Screen currentScreen = this.fetchScreenById(screen.getId());
        if (currentScreen != null) {
            currentScreen.setName(screen.getName());
            currentScreen.setFormat(screen.getFormat());

            return this.convertScreenDTO(currentScreen);
        }
        return null;
    }

    public ResultPaginationDTO fetchAllScreens(Specification<Screen> specification, Pageable pageable) {

        Page<Screen> pageScreen = this.screenRepository.findAll(specification, pageable);
        List<ScreenDTO> screens = pageScreen.stream().map(screen -> {
            ScreenDTO dto = this.convertScreenDTO(screen);
            return dto;
        }).toList();

        ResultPaginationDTO res = new ResultPaginationDTO(
                new ResultPaginationDTO.Meta(
                        pageScreen.getNumber() + 1,
                        pageScreen.getSize(),
                        pageScreen.getTotalPages(),
                        pageScreen.getTotalElements()),
                screens);

        return res;
    }

    public void deleteScreen(Screen screen){
        this.screenRepository.deleteById(screen.getId());
    }

    public ScreenDTO convertScreenDTO(Screen screen) {

        List<Schedule> schedules = this.scheduleRepository.findByScreenId(screen.getId());

        List<CreateScheduleDTO> scheduleDTOs = schedules.stream().map(schedule -> new CreateScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getCreatedAt(),
                schedule.getCreatedBy(),
                new CreateScheduleDTO.ScheduleScreen(),
                new CreateScheduleDTO.ScheduleMovie(
                        schedule.getMovie().getId(),
                        schedule.getMovie().getName())))
                .toList();

        return new ScreenDTO(
                screen.getId(),
                screen.getName(),
                screen.getCreatedAt(),
                screen.getCreatedBy(),
                screen.getUpdatedAt(),
                screen.getUpdatedBy(),
                scheduleDTOs);
    }

}
