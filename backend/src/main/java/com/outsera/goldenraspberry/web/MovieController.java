package com.outsera.goldenraspberry.web;

import com.outsera.goldenraspberry.service.MovieQueryService;
import com.outsera.goldenraspberry.web.dto.MovieResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieQueryService movieQueryService;

    public MovieController(MovieQueryService movieQueryService) {
        this.movieQueryService = movieQueryService;
    }

    @GetMapping
    public List<MovieResponse> list(@RequestParam(required = false) Boolean winner) {
        return movieQueryService.list(winner);
    }

    @GetMapping("/{id}")
    public MovieResponse getById(@PathVariable Long id) {
        return movieQueryService.getById(id);
    }
}
