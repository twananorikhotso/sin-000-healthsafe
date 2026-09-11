package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        assertEquals("5", reader.cleanBedCount("five"));
        assertEquals("0", reader.cleanBedCount("-5"));
        assertEquals("0", reader.cleanBedCount("2023"));
        assertEquals("500", reader.cleanBedCount("500"));
        assertEquals("0", reader.cleanBedCount("501"));
    }

    @Test
    void shouldPreserveValidationMessages() throws IOException {
        WardCsvReader reader = new WardCsvReader();

        reader.readWards();

        List<String> messages = reader.getValidationMessages();

        assertFalse(messages.isEmpty());
    }
}