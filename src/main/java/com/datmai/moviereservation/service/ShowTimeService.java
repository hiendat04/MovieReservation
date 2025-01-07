package com.datmai.moviereservation.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Movie;
import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.domain.ShowTime;
import com.datmai.moviereservation.repository.ShowTimeRepository;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.schedule.FetchScheduleDTO;
import com.datmai.moviereservation.common.dto.response.showtime.ShowTimeDTO;
import com.datmai.moviereservation.exception.ExistingException;

@Service
@RequiredArgsConstructor
public class ShowTimeService {
    private final ShowTimeRepository showTimeRepository;
    private final MovieService movieService;
    private final ScheduleService scheduleService;
    private final ScreenService screenService;


    public boolean isShowTimeExist(LocalTime time, Schedule schedule) {
        return this.showTimeRepository.existsByTimeAndSchedule(time, schedule);
    }

    public ShowTimeDTO createShowTime(ShowTime time) {
        ShowTime newTime =  this.showTimeRepository.save(time);
        return this.convertShowTimeDTO(newTime);
    }

    public void validateCreateShowTimeReq(ShowTime time) {
        List<String> errors = new ArrayList<>();
        // Check if schedule exists
        if (this.scheduleService.fetchScheduleById(time.getSchedule().getId()) == null) {
            errors.add("Schedule id = " + time.getSchedule().getId() + " does not exist");
        }

        // Then check if show time exists in that schedule
        if (this.isShowTimeExist(time.getTime(), time.getSchedule())) {
            errors.add(
                    "Show time at " + time.getTime() + " on "
                            + this.scheduleService.fetchScheduleById(time.getSchedule().getId()).getDate()
                            + " already exists");
        }

        // Finally check if new show time overlap
        String overlapError = this.checkShowTimeOverlap(time.getTime(), time.getSchedule().getId(), time.getId());
        if(overlapError != null) {
            errors.add(overlapError);
        }

        if (!errors.isEmpty()) {
            throw new ExistingException(errors);
        }
    }

    public void validateUpdateShowTimeReq(ShowTime showTime) {
        List<String> errors = new ArrayList<>();
        // Check if show time exist by id
        if (this.fetchShowTimeById(showTime.getId()) == null) {
            errors.add("Show time id = " + showTime.getId() + " does not exist");
        }

        // Check if request update show time already exists
        if (this.isShowTimeExist(showTime.getTime(), showTime.getSchedule())) {
            errors.add(
                    "Show time at " + showTime.getTime() + " on "
                            + this.scheduleService.fetchScheduleById(showTime.getSchedule().getId()).getDate()
                            + " already exists");
        }

        // Check if the updated show time will overlap
        Long scheduleId = this.fetchShowTimeById(showTime.getId()).getSchedule().getId();
        String overlapError = this.checkShowTimeOverlap(showTime.getTime(), scheduleId, showTime.getId());
        if(overlapError != null) {
            errors.add(overlapError);
        }

        if (!errors.isEmpty()) {
            throw new ExistingException(errors);
        }
    }

    public String checkShowTimeOverlap(LocalTime newTime, Long scheduleId, Long showTimeId) throws ExistingException {
        // // Get the duration of the movie in the schedule
        Schedule schedule = this.scheduleService.fetchScheduleById(scheduleId);
        int movieDurationMinutes = this.movieService.fetchMovieById(schedule.getMovie().getId()).getDuration();

        // Find nearest lower and upper bounds
        Optional<ShowTime> lowerBound = showTimeRepository.findNearestLowerBound(newTime, scheduleId, showTimeId);
        Optional<ShowTime> upperBound = showTimeRepository.findNearestUpperBound(newTime, scheduleId, showTimeId);

        // Check for conflicts with lower bound
        if (lowerBound.isPresent()) {
            LocalTime lowerEndTime = lowerBound.get().getTime().plusMinutes(movieDurationMinutes + 29);
            if (!newTime.isAfter(lowerEndTime)) {
                return "The new showtime overlaps with the previous showtime. Ensure a 30-minute gap.";
            }
        }

        // Check for conflicts with upper bound
        if (upperBound.isPresent()) {
            LocalTime upperStartTime = upperBound.get().getTime().minusMinutes(movieDurationMinutes + 29);
            if (!newTime.isBefore(upperStartTime)) {
                return "The new showtime overlaps with the previous showtime. Ensure a 30-minute gap.";
            }
        }
        return null;
    }

    public void validateShowTimeExist(Long id) {
        ShowTime showTime = this.fetchShowTimeById(id);
        if (showTime == null) {
            throw new ExistingException(
                    List.of("Show time id = " + id + " does not exist"));
        }
    }

    public ShowTimeDTO convertShowTimeDTO(ShowTime showTime) {
        Schedule schedule = this.scheduleService.fetchScheduleById(showTime.getSchedule().getId());
        Movie movie = this.movieService.fetchMovieById(schedule.getMovie().getId());
        Screen screen = this.screenService.fetchScreenById(schedule.getScreen().getId());

        FetchScheduleDTO scheduleDTO = new FetchScheduleDTO(schedule.getDate(),
                new FetchScheduleDTO.ScheduleMovie(movie.getId(), movie.getName(), movie.getPoster(), movie.getGenre()),
                new FetchScheduleDTO.ScheduleScreen(screen.getId(), screen.getName()));

        ShowTimeDTO res = new ShowTimeDTO(
                showTime.getId(),
                showTime.getTime(),
                scheduleDTO);

        return res;
    }

    public ShowTime fetchShowTimeById(long id) {
        ShowTime showTime = this.showTimeRepository.findById(id).isPresent()
                ? this.showTimeRepository.findById(id).get()
                : null;
        return showTime;
    }

    public ShowTimeDTO updateShowTime(ShowTime showTime) {
        // Get the showtime that will be updated
        ShowTime currentShowTime = this.fetchShowTimeById(showTime.getId());
        if (currentShowTime != null) {
            // Set only the updated time
            currentShowTime.setTime(showTime.getTime());

            // Finally update
            currentShowTime = this.showTimeRepository.save(currentShowTime);
        }
        // Convert DTO for response
        ShowTimeDTO res = this.convertShowTimeDTO(currentShowTime);
        return res;
    }

    public ResultPaginationDTO fetchAllShowTimes(Specification<ShowTime> spec, Pageable pageable) {
        Page<ShowTime> pageTime = this.showTimeRepository.findAll(spec, pageable);
        List<ShowTimeDTO> showTimes = pageTime.getContent().stream().map(time -> {
            ShowTimeDTO showTimeDTO = this.convertShowTimeDTO(time);

            return showTimeDTO;
        }).toList();

        ResultPaginationDTO res = new ResultPaginationDTO(
                new ResultPaginationDTO.Meta(pageTime.getNumber() + 1, pageTime.getSize(), pageTime.getTotalPages(),
                        pageTime.getTotalElements()),
                showTimes);

        return res;
    }

    public void deleteShowTime(Long id) {
        this.showTimeRepository.deleteById(id);
    }
}
