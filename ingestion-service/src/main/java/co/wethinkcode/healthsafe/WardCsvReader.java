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

    public List<Ward> readWards() throws IOException {
        List<String> lines = readLines();
        List<Ward> wards = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            String[] columns = line.split(",", -1);

            Ward ward = new Ward(
                    columns[0],
                    columns[1],
                    columns[2],
                    columns[3]
            );

            wards.add(ward);
        }

        return wards;
    }
}