package com.outsera.goldenraspberry.repository;

import com.outsera.goldenraspberry.domain.Movie;
import com.outsera.goldenraspberry.domain.WinningProducerYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findAllByOrderByReleaseYearAscTitleAscIdAsc();

    List<Movie> findByWinnerTrueOrderByReleaseYearAscTitleAscIdAsc();

    List<Movie> findByWinnerFalseOrderByReleaseYearAscTitleAscIdAsc();

    @Query("""
            select new com.outsera.goldenraspberry.domain.WinningProducerYear(p.name, m.releaseYear)
            from Movie m
            join m.producers p
            where m.winner = true
            """)
    List<WinningProducerYear> findWinningProducerYears();
}
