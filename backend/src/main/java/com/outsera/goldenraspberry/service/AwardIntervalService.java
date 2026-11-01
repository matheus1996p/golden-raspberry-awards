package com.outsera.goldenraspberry.service;

import com.outsera.goldenraspberry.domain.WinningProducerYear;
import com.outsera.goldenraspberry.repository.MovieRepository;
import com.outsera.goldenraspberry.web.dto.AwardIntervalResponse;
import com.outsera.goldenraspberry.web.dto.ProducerAwardIntervalResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AwardIntervalService {

    private static final Comparator<ProducerAwardIntervalResponse> INTERVAL_ORDER =
            Comparator.comparingInt(ProducerAwardIntervalResponse::previousWin)
                    .thenComparingInt(ProducerAwardIntervalResponse::followingWin)
                    .thenComparing(ProducerAwardIntervalResponse::producer);

    private final MovieRepository movieRepository;

    public AwardIntervalService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Para cada produtor com dois ou mais prêmios, considera apenas pares consecutivos
     * de vitórias. O resultado traz todos os pares cujo intervalo é o menor e todos
     * cujo intervalo é o maior.
     */
    @Transactional(readOnly = true)
    public AwardIntervalResponse calculate() {
        Map<String, List<Integer>> yearsByProducer = new LinkedHashMap<>();
        for (WinningProducerYear win : movieRepository.findWinningProducerYears()) {
            yearsByProducer.computeIfAbsent(win.producer(), key -> new ArrayList<>()).add(win.year());
        }

        List<ProducerAwardIntervalResponse> intervals = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : yearsByProducer.entrySet()) {
            List<Integer> years = entry.getValue();
            years.sort(Comparator.naturalOrder());
            for (int index = 0; index < years.size() - 1; index++) {
                int previousWin = years.get(index);
                int followingWin = years.get(index + 1);
                intervals.add(new ProducerAwardIntervalResponse(
                        entry.getKey(),
                        followingWin - previousWin,
                        previousWin,
                        followingWin));
            }
        }

        if (intervals.isEmpty()) {
            return new AwardIntervalResponse(List.of(), List.of());
        }

        int shortest = intervals.stream().mapToInt(ProducerAwardIntervalResponse::interval).min().orElseThrow();
        int longest = intervals.stream().mapToInt(ProducerAwardIntervalResponse::interval).max().orElseThrow();

        return new AwardIntervalResponse(
                filterByInterval(intervals, shortest),
                filterByInterval(intervals, longest));
    }

    private List<ProducerAwardIntervalResponse> filterByInterval(
            List<ProducerAwardIntervalResponse> intervals,
            int interval) {
        return intervals.stream()
                .filter(candidate -> candidate.interval() == interval)
                .sorted(INTERVAL_ORDER)
                .toList();
    }
}
