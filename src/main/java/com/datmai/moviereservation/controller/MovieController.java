package com.datmai.moviereservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.Movie;
import com.datmai.moviereservation.service.MovieService;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Movie Controller")
public class MovieController {

    private final MovieService movieService;


    @PostMapping("/movies")
    @ApiMessage("Create movie successfully")
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) throws ExistingException {

        // Check if movie exist by name and director
        if (this.movieService.isMovieExist(movie.getName(), movie.getDirector())) {
            throw new ExistingException(
                    "Movie " + movie.getName() + " of director " + movie.getDirector() + " already exists");
        }
        Movie newMovie = this.movieService.createMovie(movie);

        return ResponseEntity.status(HttpStatus.CREATED).body(newMovie);
    }

    @PutMapping("/movies")
    @ApiMessage("Update movie successfully")
    public ResponseEntity<Movie> updateMovie(@RequestBody Movie requestMovie) throws ExistingException {

        // Check if movie id exists
        Movie currentMovie = this.movieService.fetchMovieById(requestMovie.getId());
        if (currentMovie == null) {
            throw new ExistingException("Movie id = " + requestMovie.getId() + " does not exist");
        }

        Movie updatedMovie = this.movieService.updateMovie(requestMovie);
        return ResponseEntity.ok().body(updatedMovie);
    }

    @GetMapping("/movies/{id}")
    @ApiMessage("Fetch movie successfully")
    public ResponseEntity<Movie> fetchAMovie(@PathVariable long id) throws ExistingException {
        // Check if movie id exists
        Movie movie = this.movieService.fetchMovieById(id);
        if (movie == null) {
            throw new ExistingException("Movie id = " + id + " does not exist");
        }
        return ResponseEntity.ok().body(movie);
    }

    @GetMapping("/movies")
    @ApiMessage("Fetch movies successfully")
    public ResponseEntity<ResultPaginationDTO> fetchAllMovies(@Filter Specification<Movie> spec, Pageable pageable) {
        ResultPaginationDTO res = this.movieService.fetchAllMovies(spec, pageable);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/movies/{id}")
    @ApiMessage("Delete movie successfully")
    public ResponseEntity<Void> deleteMovie(@PathVariable long id) throws ExistingException {
        // Check if movie id exists
        Movie movie = this.movieService.fetchMovieById(id);
        if (movie == null) {
            throw new ExistingException("Movie id = " + id + " does not exist");
        }
        this.movieService.deleteMovie(id);
        return ResponseEntity.ok(null);
    }

}
