package ua.antonchuk.medicine.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

final class StrictErrorListener implements ErrorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrictErrorListener.class);

    @Override
    public void warning(TransformerException exception) {
        LOGGER.warn("XSL warning: {}", exception.getMessage());
    }

    @Override
    public void error(TransformerException exception) throws TransformerException {
        throw exception;
    }

    @Override
    public void fatalError(TransformerException exception) throws TransformerException {
        throw exception;
    }
}