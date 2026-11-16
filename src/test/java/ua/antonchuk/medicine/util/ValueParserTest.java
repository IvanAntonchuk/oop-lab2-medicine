package ua.antonchuk.medicine.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueParserTest {

    private static final double DELTA = 1e-9;

    @Test
    void parseDateReturnsDate() {
        assertEquals(LocalDate.of(2021, 3, 15), ValueParser.parseDate("2021-03-15", "Date"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"15.03.2021", "2021-02-30", "abc"})
    void parseDateRejectsInvalidValue(String value) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ValueParser.parseDate(value, "Date"));

        assertEquals("Date has invalid value: " + value, exception.getMessage());
    }

    @Test
    void parseIntReturnsNumberAndIgnoresSurroundingSpaces() {
        assertEquals(16, ValueParser.parseInt(" 16 ", "Quantity"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.5", "abc", "99999999999"})
    void parseIntRejectsInvalidValue(String value) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ValueParser.parseInt(value, "Quantity"));

        assertEquals("Quantity has invalid value: " + value, exception.getMessage());
    }

    @Test
    void parseDoubleReturnsNumber() {
        assertEquals(0.25, ValueParser.parseDouble("0.25", "Amount"), DELTA);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1,5"})
    void parseDoubleRejectsInvalidValue(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> ValueParser.parseDouble(value, "Amount"));
    }

    @Test
    void parseDecimalKeepsScale() {
        assertEquals(new BigDecimal("85.50"), ValueParser.parseDecimal("85.50", "Price"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1,5"})
    void parseDecimalRejectsInvalidValue(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> ValueParser.parseDecimal(value, "Price"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void parseIntRejectsNullOrBlankValue(String value) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ValueParser.parseInt(value, "Quantity"));

        assertEquals("Quantity must not be null or blank", exception.getMessage());
    }

    @Test
    void everyParserRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> ValueParser.parseDate(null, "Date"));
        assertThrows(IllegalArgumentException.class, () -> ValueParser.parseInt(null, "Int"));
        assertThrows(IllegalArgumentException.class,
                () -> ValueParser.parseDouble(null, "Double"));
        assertThrows(IllegalArgumentException.class,
                () -> ValueParser.parseDecimal(null, "Decimal"));
    }
}