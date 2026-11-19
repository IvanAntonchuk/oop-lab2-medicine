package ua.antonchuk.medicine.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ConfigExceptionTest {

    @Test
    void messageConstructorStoresMessageWithoutCause() {
        ConfigException exception = new ConfigException("Something failed");

        assertEquals("Something failed", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void messageAndCauseConstructorStoresBoth() {
        Exception cause = new IllegalStateException("root cause");

        ConfigException exception = new ConfigException("Something failed", cause);

        assertEquals("Something failed", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}