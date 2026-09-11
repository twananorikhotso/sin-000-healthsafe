package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WardCsvReaderTest {

    @Test
    void shouldNormalizeWardId() {
        WardCsvReader reader = new WardCsvReader();

        assertEquals("W-02", reader.cleanWardId("  w-02  "));
    }

    @Test
    void shouldNormalizeWingName() {
        WardCsvReader reader = new WardCsvReader();

        assertEquals("South Wing", reader.cleanWingName("  South  Wing  "));
    }

    @Test
    void shouldNormalizeDepartment() {
        WardCsvReader reader = new WardCsvReader();

        assertEquals("Paediatrics", reader.cleanDepartment("PAEDIATRICS"));
        assertEquals("ICU", reader.cleanDepartment("icu"));
    }

    @Test
    void shouldHandleMissingValues() {
        WardCsvReader reader = new WardCsvReader();

        assertEquals("Unknown", reader.cleanWingName("N/A"));
        assertEquals("Unknown", reader.cleanDepartment("TBD"));
    }

    @Test
    void shouldHandleInvalidBedCounts() {
        WardCsvReader reader = new WardCsvReader();

        assertEquals("0", reader.cleanBedCount("full"));
        assertEquals("0", reader.cleanBedCount("-5"));
        assertEquals("0", reader.cleanBedCount("2023"));
        assertEquals("500", reader.cleanBedCount("500"));
        assertEquals("0", reader.cleanBedCount("501"));
    }
}