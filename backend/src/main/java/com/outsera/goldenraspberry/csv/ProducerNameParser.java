package com.outsera.goldenraspberry.csv;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ProducerNameParser {

    private static final Pattern AND_SEPARATOR = Pattern.compile("(?i)\\s+and\\s+");

    public List<String> parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }

        String normalized = AND_SEPARATOR.matcher(raw).replaceAll(",");
        Set<String> names = new LinkedHashSet<>();
        for (String part : normalized.split(",")) {
            String name = part.trim();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }
        return new ArrayList<>(names);
    }
}
