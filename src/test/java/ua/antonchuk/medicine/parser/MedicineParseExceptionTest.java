package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class MedicineParseExceptionTest {

    @Test
    void messageConstructorStoresMessageWithoutCause() {
        MedicineParseException exception = new MedicineParseException("Something failed");

        assertEquals("Something failed", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void messageAndCauseConstructorStoresBoth() {
        Exception cause = new IllegalStateException("root cause");

        MedicineParseException exception = new MedicineParseException("Something failed", cause);

        assertEquals("Something failed", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}