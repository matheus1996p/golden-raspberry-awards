package com.outsera.goldenraspberry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "release_year", nullable = false)
    private int releaseYear;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(nullable = false)
    private boolean winner;

    @ManyToMany
    @JoinTable(
            name = "movie_studios",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "studio_id"))
    @OrderColumn(name = "lineup_order")
    @Fetch(FetchMode.SUBSELECT)
    private List<Studio> studios = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "movie_producers",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "producer_id"))
    @OrderColumn(name = "lineup_order")
    @Fetch(FetchMode.SUBSELECT)
    private List<Producer> producers = new ArrayList<>();

    protected Movie() {
    }

    public Movie(int releaseYear, String title, boolean winner) {
        this.releaseYear = releaseYear;
        this.title = title;
        this.winner = winner;
    }

    public void addStudio(Studio studio) {
        studios.add(studio);
    }

    public void addProducer(Producer producer) {
        producers.add(producer);
    }

    public Long getId() {
        return id;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public boolean isWinner() {
        return winner;
    }

    public List<Studio> getStudios() {
        return studios;
    }

    public List<Producer> getProducers() {
        return producers;
    }
}
