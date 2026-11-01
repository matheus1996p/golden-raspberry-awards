package com.outsera.goldenraspberry;

import com.outsera.goldenraspberry.web.dto.AwardIntervalResponse;
import com.outsera.goldenraspberry.web.dto.MovieResponse;
import com.outsera.goldenraspberry.web.dto.ProducerAwardIntervalResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class AwardIntervalsIntegrationTest {

    @DynamicPropertySource
    static void isolateDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                "jdbc:h2:mem:proposal-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    }

    @Autowired
    private TestRestTemplate rest;

    @Test
    void returnsShortestAndLongestConsecutiveWinIntervalsFromTheProposalCsv() {
        ResponseEntity<AwardIntervalResponse> response =
                rest.getForEntity("/api/producers/award-intervals", AwardIntervalResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();

        AwardIntervalResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.min()).containsExactly(
                new ProducerAwardIntervalResponse("Joel Silver", 1, 1990, 1991));
        assertThat(body.max()).containsExactly(
                new ProducerAwardIntervalResponse("Matthew Vaughn", 13, 2002, 2015));
    }

    @Test
    void loadsEveryMovieAndPreservesWinnerStudiosAndProducers() {
        ResponseEntity<MovieResponse[]> response = rest.getForEntity("/api/movies", MovieResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MovieResponse[] movies = response.getBody();
        assertThat(movies).isNotNull().hasSize(206);

        MovieResponse cruising = findByTitle(movies, "Cruising");
        assertThat(cruising.year()).isEqualTo(1980);
        assertThat(cruising.winner()).isFalse();
        assertThat(cruising.studios()).containsExactly("Lorimar Productions", "United Artists");
        assertThat(cruising.producers()).containsExactly("Jerry Weintraub");

        MovieResponse fordFairlane = findByTitle(movies, "The Adventures of Ford Fairlane");
        assertThat(fordFairlane.winner()).isTrue();
        assertThat(fordFairlane.producers()).containsExactly("Steven Perry", "Joel Silver");

        MovieResponse cherryMoon = findByTitle(movies, "Under the Cherry Moon");
        assertThat(cherryMoon.winner()).isTrue();
        assertThat(cherryMoon.producers()).containsExactly("Bob Cavallo", "Joe Ruffalo", "Steve Fargnoli");

        MovieResponse cats = findByTitle(movies, "Cats");
        assertThat(cats.winner()).isTrue();
        assertThat(cats.year()).isEqualTo(2019);
        assertThat(cats.producers()).containsExactly("Debra Hayward", "Tim Bevan", "Eric Fellner", "Tom Hooper");

        assertThat(Arrays.stream(movies).filter(MovieResponse::winner)).hasSize(42);
    }

    @Test
    void filtersWinnersAndReturnsNotFoundForAnUnknownMovie() {
        ResponseEntity<MovieResponse[]> winners =
                rest.getForEntity("/api/movies?winner=true", MovieResponse[].class);
        assertThat(winners.getBody()).isNotNull().allMatch(MovieResponse::winner);

        ResponseEntity<MovieResponse[]> nominees =
                rest.getForEntity("/api/movies?winner=false", MovieResponse[].class);
        assertThat(nominees.getBody()).isNotNull().noneMatch(MovieResponse::winner);

        MovieResponse first = winners.getBody()[0];
        ResponseEntity<MovieResponse> found =
                rest.getForEntity("/api/movies/" + first.id(), MovieResponse.class);
        assertThat(found.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(found.getBody()).isNotNull();
        assertThat(found.getBody().title()).isEqualTo(first.title());

        ResponseEntity<String> missing = rest.getForEntity("/api/movies/999999", String.class);
        assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<String> notAllowed = rest.exchange("/api/movies", HttpMethod.POST, null, String.class);
        assertThat(notAllowed.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    private MovieResponse findByTitle(MovieResponse[] movies, String title) {
        return Arrays.stream(movies)
                .filter(movie -> title.equals(movie.title()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Filme não encontrado: " + title));
    }
}
