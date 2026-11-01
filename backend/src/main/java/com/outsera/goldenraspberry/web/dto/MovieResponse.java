package com.outsera.goldenraspberry.web.dto;

import com.outsera.goldenraspberry.domain.Movie;
import com.outsera.goldenraspberry.domain.Producer;
import com.outsera.goldenraspberry.domain.Studio;

import java.util.List;

public record MovieResponse(
        Long id,
        int year,
        String title,
        List<String> studios,
        List<String> producers,
        boolean winner) {

    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getReleaseYear(),
                movie.getTitle(),
                movie.getStudios().stream().map(Studio::getName).toList(),
                movie.getProducers().stream().map(Producer::getName).toList(),
                movie.isWinner());
    }
}
