package com.outsera.goldenraspberry.csv;

import java.util.ArrayList;
import java.util.List;

public final class SemicolonCsvParser {

    private SemicolonCsvParser() {
    }

    public static List<String> parse(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (inQuotes) {
                if (character == '"') {
                    if (index + 1 < line.length() && line.charAt(index + 1) == '"') {
                        current.append('"');
                        index++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(character);
                }
            } else if (character == '"') {
                inQuotes = true;
            } else if (character == ';') {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("Aspas não fechadas no CSV");
        }

        fields.add(current.toString().trim());
        return fields;
    }
}
