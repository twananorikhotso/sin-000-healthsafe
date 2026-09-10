package co.wethinkcode.healthsafe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WardCsvReader {

    public List<String> readLines() throws IOException {
        InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream("wards-outdated.csv");

        if (inputStream == null) {
            throw new IOException("wards-outdated.csv not found in resources");
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(inputStream))) {

            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    private String cleanText(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private String cleanWardId(String value) {
        if (isMissingValue(value)) {
            return "Unknown";
        }

        return cleanText(value).toUpperCase();
    }

    private String cleanWingName(String value) {
        if (isMissingValue(value)) {
            return "Unknown";
        }

        String cleaned = cleanText(value).toLowerCase();

        String[] words = cleaned.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return result.toString().trim();
    }

    private String cleanDepartment(String value) {
        if (isMissingValue(value)) {
            return "Unknown";
        }

        String cleaned = cleanText(value);

        if (cleaned.equalsIgnoreCase("ICU")) {
            return "ICU";
        }

        cleaned = cleaned.toLowerCase();

        String[] words = cleaned.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return result.toString().trim();
    }

    private String cleanBedCount(String value) {
        String cleaned = cleanText(value);

        if (cleaned.equalsIgnoreCase("N/A")
                || cleaned.equalsIgnoreCase("TBD")
                || cleaned.equalsIgnoreCase("unknown")
                || cleaned.equalsIgnoreCase("full")
                || cleaned.isBlank()) {
            return "0";
        }

        try {
            int beds = Integer.parseInt(cleaned);

            if (beds < 0) {
                return "0";
            }

            return String.valueOf(beds);

        } catch (NumberFormatException e) {
            return "0";
        }
    }

    public List<Ward> readWards() throws IOException {
        List<String> lines = readLines();
        List<Ward> wards = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            String[] columns = line.split(",", -1);

            Ward ward = new Ward(
                    cleanWardId(columns[0]),
                    cleanWingName(columns[1]),
                    cleanDepartment(columns[2]),
                    cleanBedCount(columns[3])
            );

            wards.add(ward);
        }

        return wards;
    }

    private boolean isMissingValue(String value) {
        if (value == null) {
            return true;
        }

        String cleaned = value.trim();

        return cleaned.isBlank()
                || cleaned.equalsIgnoreCase("N/A")
                || cleaned.equalsIgnoreCase("TBD")
                || cleaned.equalsIgnoreCase("unknown");
    }
}