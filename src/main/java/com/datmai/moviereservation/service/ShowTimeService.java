package com.datmai.moviereservation.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Movie;
import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.domain.ShowTime;
import com.datmai.moviereservation.repository.ShowTimeRepository;
import com.datmai.moviereservation.util.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.util.dto.response.schedule.FetchScheduleDTO;
import com.datmai.moviereservation.util.dto.response.showtime.ShowTimeDTO;
import com.datmai.moviereservation.util.error.ExistingException;

@Service
public class ShowTimeService {
    private final ShowTimeRepository showTimeRepository;
    private final MovieService movieService;
    private final ScheduleService scheduleService;
    private final ScreenService screenService;

    public ShowTimeService(ShowTimeRepository showTimeRepository, MovieService movieService,
            ScheduleService scheduleService, ScreenService screenService) {
        this.showTimeRepository = showTimeRepository;
        this.movieService = movieService;
        this.scheduleService = scheduleService;
        this.screenService = screenService;
    }

    public boolean isShowTimeExist(LocalTime time, Schedule schedule) {
        return this.showTimeRepository.existsByTimeAndSchedule(time, schedule);
    }

    public ShowTime createShowTime(ShowTime time) {
        return this.showTimeRepository.save(time);
    }

    public void isShowTimeOverlap(LocalTime newTime, Long scheduleId, Long showTimeId) throws ExistingException {
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
                throw new ExistingException(
                        "The new showtime overlaps with the previous showtime. Ensure a 30-minute gap.");
            }
        }

        // Check for conflicts with upper bound
        if (upperBound.isPresent()) {
            LocalTime upperStartTime = upperBound.get().getTime().minusMinutes(movieDurationMinutes + 29);
            if (!newTime.isBefore(upperStartTime)) {
                throw new ExistingException(
                        "The new showtime overlaps with the next showtime. Ensure a 30-minute gap.");
            }
        }
    }

    public ShowTimeDTO convertShowTimeDTO(ShowTime showTime) {
        Schedule schedule = this.scheduleService.fetchScheduleById(showTime.getSchedule().getId());
        Movie movie = this.movieService.fetchMovieById(schedule.getMovie().getId());
        Screen screen = this.screenService.fetchScreenById(schedule.getScreen().getId());

        FetchScheduleDTO scheduleDTO = new FetchScheduleDTO(schedule.getDate(), schedule.getFormat(),
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
        // Get the show time that will be updated
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

    public void deleteShowTime(ShowTime showTime) {
        this.showTimeRepository.deleteById(showTime.getId());
    }
}
