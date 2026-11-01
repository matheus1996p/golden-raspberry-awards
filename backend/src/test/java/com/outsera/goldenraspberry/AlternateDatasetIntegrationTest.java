package com.outsera.goldenraspberry;

import com.outsera.goldenraspberry.web.dto.AwardIntervalResponse;
import com.outsera.goldenraspberry.web.dto.MovieResponse;
import com.outsera.goldenraspberry.web.dto.ProducerAwardIntervalResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestPropertySource(properties = "awards.csv-location=classpath:datasets/consecutive-intervals.csv")
class AlternateDatasetIntegrationTest {

    @DynamicPropertySource
    static void isolateDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                "jdbc:h2:mem:alternate-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    }

    @Autowired
    private TestRestTemplate rest;

    @Test
    void usesConsecutiveWinsAndKeepsEveryTiedProducer() {
        ResponseEntity<AwardIntervalResponse> response =
                rest.getForEntity("/api/producers/award-intervals", AwardIntervalResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AwardIntervalResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.min()).containsExactly(
                new ProducerAwardIntervalResponse("Carla Dias", 0, 2000, 2000),
                new ProducerAwardIntervalResponse("Eva Nunes", 0, 2012, 2012));
        assertThat(body.max()).containsExactly(
                new ProducerAwardIntervalResponse("Bruno Costa", 19, 1980, 1999),
                new ProducerAwardIntervalResponse("Ana Silva", 19, 1991, 2010));
    }

    @Test
    void ignoresNominationsThatDidNotWin() {
        ResponseEntity<MovieResponse[]> response = rest.getForEntity("/api/movies", MovieResponse[].class);
        assertThat(response.getBody()).isNotNull().hasSize(12);

        MovieResponse sharedWin = Arrays.stream(response.getBody())
                .filter(movie -> "Film F".equals(movie.title()))
                .findFirst()
                .orElseThrow();
        assertThat(sharedWin.producers()).containsExactly("Carla Dias", "Diego Alves");
        assertThat(sharedWin.winner()).isTrue();
    }
}
