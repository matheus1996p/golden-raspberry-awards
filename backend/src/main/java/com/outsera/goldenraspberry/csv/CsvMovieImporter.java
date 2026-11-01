package com.outsera.goldenraspberry.csv;

import com.outsera.goldenraspberry.config.AwardsProperties;
import com.outsera.goldenraspberry.domain.Movie;
import com.outsera.goldenraspberry.domain.Producer;
import com.outsera.goldenraspberry.domain.Studio;
import com.outsera.goldenraspberry.repository.MovieRepository;
import com.outsera.goldenraspberry.repository.ProducerRepository;
import com.outsera.goldenraspberry.repository.StudioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class CsvMovieImporter {

    private static final Logger log = LoggerFactory.getLogger(CsvMovieImporter.class);

    private final AwardsProperties properties;
    private final ResourceLoader resourceLoader;
    private final MovieRepository movieRepository;
    private final ProducerRepository producerRepository;
    private final StudioRepository studioRepository;
    private final ProducerNameParser producerNameParser;

    public CsvMovieImporter(
            AwardsProperties properties,
            ResourceLoader resourceLoader,
            MovieRepository movieRepository,
            ProducerRepository producerRepository,
            StudioRepository studioRepository,
            ProducerNameParser producerNameParser) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.movieRepository = movieRepository;
        this.producerRepository = producerRepository;
        this.studioRepository = studioRepository;
        this.producerNameParser = producerNameParser;
    }

    @Transactional
    public int importCsv() {
        Resource resource = resourceLoader.getResource(properties.csvLocation());
        if (!resource.exists()) {
            throw new IllegalStateException("CSV de filmes não encontrado: " + properties.csvLocation());
        }

        int imported = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            boolean headerChecked = false;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
                    line = line.substring(1);
                }
                if (line.isBlank()) {
                    continue;
                }
                if (!headerChecked) {
                    headerChecked = true;
                    if (line.toLowerCase(Locale.ROOT).startsWith("year;")) {
                        continue;
                    }
                }
                importLine(line, lineNumber);
                imported++;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao ler o CSV de filmes", exception);
        }

        log.info("Importados {} filmes a partir de {}", imported, properties.csvLocation());
        return imported;
    }

    private void importLine(String line, int lineNumber) {
        List<String> fields;
        try {
            fields = SemicolonCsvParser.parse(line);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("CSV inválido na linha " + lineNumber, exception);
        }
        if (fields.size() < 4) {
            throw new IllegalStateException("Linha " + lineNumber + " do CSV está incompleta");
        }

        int year = parseYear(fields.get(0), lineNumber);
        String title = fields.get(1).trim();
        if (title.isEmpty()) {
            throw new IllegalStateException("Título vazio na linha " + lineNumber);
        }

        boolean winner = fields.size() > 4 && "yes".equalsIgnoreCase(fields.get(4).trim());
        Movie movie = new Movie(year, title, winner);
        for (String studioName : splitStudios(fields.get(2))) {
            movie.addStudio(resolveStudio(studioName));
        }
        for (String producerName : producerNameParser.parse(fields.get(3))) {
            movie.addProducer(resolveProducer(producerName));
        }
        movieRepository.save(movie);
    }

    private int parseYear(String rawYear, int lineNumber) {
        try {
            return Integer.parseInt(rawYear.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Ano inválido na linha " + lineNumber, exception);
        }
    }

    private List<String> splitStudios(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        Set<String> names = new LinkedHashSet<>();
        for (String part : raw.split(",")) {
            String name = part.trim();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }
        return new ArrayList<>(names);
    }

    private Studio resolveStudio(String name) {
        return studioRepository.findByName(name).orElseGet(() -> studioRepository.save(new Studio(name)));
    }

    private Producer resolveProducer(String name) {
        return producerRepository.findByName(name).orElseGet(() -> producerRepository.save(new Producer(name)));
    }
}
