package com.outsera.goldenraspberry.csv;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieCsvLoader implements ApplicationRunner {

    private final CsvMovieImporter importer;

    public MovieCsvLoader(CsvMovieImporter importer) {
        this.importer = importer;
    }

    @Override
    public void run(ApplicationArguments args) {
        importer.importCsv();
    }
}
