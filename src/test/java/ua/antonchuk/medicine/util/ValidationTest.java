package ua.antonchuk.medicine.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    private static final double DELTA = 1e-9;

    @Test
    void requireNonNullReturnsSameValue() {
        String value = "abc";

        assertSame(value, Validation.requireNonNull(value, "Field"));
    }

    @Test
    void requireNonNullRejectsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validation.requireNonNull(null, "Field"));

        assertEquals("Field must not be null", exception.getMessage());
    }

    @Test
    void requireTextTrimsValue() {
        assertEquals("abc", Validation.requireText("  abc  ", "Field"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void requireTextRejectsNullOrBlank(String value) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validation.requireText(value, "Field"));

        assertEquals("Field must not be null or blank", exception.getMessage());
    }

    @Test
    void requirePositiveIntReturnsValue() {
        assertEquals(5, Validation.requirePositive(5, "Field"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void requirePositiveIntRejectsNonPositive(int value) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validation.requirePositive(value, "Field"));

        assertEquals("Field must be positive: " + value, exception.getMessage());
    }

    @Test
    void requirePositiveDoubleReturnsValue() {
        assertEquals(2.5, Validation.requirePositive(2.5, "Field"), DELTA);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.5, Double.NaN, Double.POSITIVE_INFINITY})
    void requirePositiveDoubleRejectsInvalid(double value) {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.requirePositive(value, "Field"));
    }

    @Test
    void requirePositiveBigDecimalReturnsSameValue() {
        BigDecimal value = new BigDecimal("85.50");

        assertSame(value, Validation.requirePositive(value, "Field"));
    }

    @Test
    void requirePositiveBigDecimalRejectsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validation.requirePositive((BigDecimal) null, "Field"));

        assertEquals("Field must not be null", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "-1", "-85.50"})
    void requirePositiveBigDecimalRejectsNonPositive(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.requirePositive(new BigDecimal(value), "Field"));
    }

    @Test
    void copyOfListReturnsUnmodifiableCopy() {
        List<String> source = new ArrayList<>(List.of("a", "b"));

        List<String> copy = Validation.copyOfList(source, "Field");
        source.add("c");

        assertEquals(List.of("a", "b"), copy);
        assertThrows(UnsupportedOperationException.class, () -> copy.add("d"));
    }

    @Test
    void copyOfListAllowsEmptyList() {
        assertTrue(Validation.copyOfList(Collections.<String>emptyList(), "Field").isEmpty());
    }

    @Test
    void copyOfListRejectsNullList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validation.copyOfList((List<String>) null, "Field"));

        assertEquals("Field must not be null", exception.getMessage());
    }

    @Test
    void copyOfListRejectsNullElement() {
        List<String> source = Arrays.asList("a", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validation.copyOfList(source, "Field"));

        assertEquals("Field must not contain null elements", exception.getMessage());
    }

    @Test
    void requireNonEmptyListReturnsCopy() {
        assertEquals(List.of("a"), Validation.requireNonEmptyList(List.of("a"), "Field"));
    }

    @Test
    void requireNonEmptyListRejectsEmptyList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validation.requireNonEmptyList(List.<String>of(), "Field"));

        assertEquals("Field must not be empty", exception.getMessage());
    }
}