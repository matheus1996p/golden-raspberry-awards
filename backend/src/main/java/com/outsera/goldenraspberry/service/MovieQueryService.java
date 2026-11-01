package com.outsera.goldenraspberry.service;

import com.outsera.goldenraspberry.domain.Movie;
import com.outsera.goldenraspberry.repository.MovieRepository;
import com.outsera.goldenraspberry.web.dto.MovieResponse;
import com.outsera.goldenraspberry.web.error.MovieNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MovieQueryService {

    private final MovieRepository movieRepository;

    public MovieQueryService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieResponse> list(Boolean winner) {
        List<Movie> movies;
        if (winner == null) {
            movies = movieRepository.findAllByOrderByReleaseYearAscTitleAscIdAsc();
        } else if (winner) {
            movies = movieRepository.findByWinnerTrueOrderByReleaseYearAscTitleAscIdAsc();
        } else {
            movies = movieRepository.findByWinnerFalseOrderByReleaseYearAscTitleAscIdAsc();
        }
        return movies.stream().map(MovieResponse::from).toList();
    }

    public MovieResponse getById(Long id) {
        return movieRepository.findById(id)
                .map(MovieResponse::from)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }
}
