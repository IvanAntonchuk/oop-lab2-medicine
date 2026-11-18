package ua.antonchuk.medicine.transform;

import org.junit.jupiter.api.Test;

import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StrictErrorListenerTest {

    private final StrictErrorListener listener = new StrictErrorListener();

    @Test
    void warningDoesNotThrow() {
        assertDoesNotThrow(() -> listener.warning(new TransformerException("warning")));
    }

    @Test
    void errorRethrowsTheSameException() {
        TransformerException exception = new TransformerException("error");

        TransformerException thrown = assertThrows(TransformerException.class,
                () -> listener.error(exception));

        assertSame(exception, thrown);
    }

    @Test
    void fatalErrorRethrowsTheSameException() {
        TransformerException exception = new TransformerException("fatal");

        TransformerException thrown = assertThrows(TransformerException.class,
                () -> listener.fatalError(exception));

        assertSame(exception, thrown);
    }
}