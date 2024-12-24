package com.datmai.moviereservation.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Movie;
import com.datmai.moviereservation.repository.MovieRepository;
import com.datmai.moviereservation.util.dto.response.pagination.ResultPaginationDTO;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public boolean isMovieExist(String name, String director) {
        return this.movieRepository.existsByNameAndDirector(name, director);
    }

    public Movie createMovie(Movie movie) {
        if (!this.isMovieExist(movie.getName(), movie.getDirector())) {
            return this.movieRepository.save(movie);
        }
        return null;
    }

    public Movie fetchMovieById(long id) {
        Optional<Movie> movieOptional = this.movieRepository.findById(id);
        Movie movie = movieOptional.isPresent() ? movieOptional.get() : null;
        return movie;
    }

    public Movie updateMovie(Movie requestMovie) {
        Movie currentMovie = this.fetchMovieById(requestMovie.getId());
        if (currentMovie != null) {
            currentMovie.setName(requestMovie.getName());
            currentMovie.setActors(requestMovie.getActors());
            currentMovie.setAge(requestMovie.getAge());
            currentMovie.setCountry(requestMovie.getCountry());
            currentMovie.setDescription(requestMovie.getDescription());
            currentMovie.setDirector(requestMovie.getDirector());
            currentMovie.setDuration(requestMovie.getDuration());
            currentMovie.setGenre(requestMovie.getGenre());
            currentMovie.setSchedules(requestMovie.getSchedules());
            currentMovie.setSubtitle(requestMovie.getSubtitle());

            currentMovie = this.createMovie(currentMovie);
        }
        return currentMovie;
    }

    public ResultPaginationDTO fetchAllMovies(Specification<Movie> spec, Pageable pageable) {
        Page<Movie> pageMovie = this.movieRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setCurrentPage(pageMovie.getNumber() + 1);
        meta.setPageSize(pageMovie.getSize());
        meta.setTotalElements(pageMovie.getNumberOfElements());
        meta.setTotalPages(pageMovie.getTotalPages());

        res.setMeta(meta);
        res.setResult(pageMovie.getContent());

        return res;
    }

    public void deleteMovie(long id){
        this.movieRepository.deleteById(id);
    }
}
