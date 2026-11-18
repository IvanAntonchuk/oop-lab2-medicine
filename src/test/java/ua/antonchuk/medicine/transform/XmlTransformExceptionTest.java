package ua.antonchuk.medicine.transform;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class XmlTransformExceptionTest {

    @Test
    void messageConstructorStoresMessageWithoutCause() {
        XmlTransformException exception = new XmlTransformException("Something failed");

        assertEquals("Something failed", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void messageAndCauseConstructorStoresBoth() {
        Exception cause = new IllegalStateException("root cause");

        XmlTransformException exception = new XmlTransformException("Something failed", cause);

        assertEquals("Something failed", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}