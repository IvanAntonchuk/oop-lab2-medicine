package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.MedicineGroup;
import ua.antonchuk.medicine.model.TestData;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractMedicineParserTest {

    @FunctionalInterface
    private interface DocumentAction {

        List<Medicine> run(InputStream input) throws IOException, SAXException,
                ParserConfigurationException, XMLStreamException;
    }

    private static final class RecordingValidator extends XmlValidator {

        private final List<String> calls;
        private MedicineParseException failure;

        RecordingValidator(List<String> calls) {
            super(TestFiles.SCHEMA);
            this.calls = calls;
        }

        @Override
        public void validate(Path xmlFile) {
            calls.add("validate " + xmlFile);
            if (failure != null) {
                throw failure;
            }
        }
    }

    private static final class TestParser extends AbstractMedicineParser {

        private final DocumentAction action;

        TestParser(XmlValidator validator, DocumentAction action) {
            super(validator);
            this.action = action;
        }

        @Override
        protected List<Medicine> parseDocument(InputStream input) throws IOException,
                SAXException, ParserConfigurationException, XMLStreamException {
            return action.run(input);
        }
    }

    private final List<String> calls = new ArrayList<>();
    private final RecordingValidator validator = new RecordingValidator(calls);

    private static Medicine createMedicine() {
        return new Medicine("MED-001", "Amoxicillin", "Farmak", MedicineGroup.ANTIBIOTIC,
                List.of("Ospamox"), List.of(TestData.version(MedicineForm.CAPSULE)));
    }

    private String errorOf(DocumentAction action) {
        return assertThrows(MedicineParseException.class,
                () -> new TestParser(validator, action).parse(TestFiles.SAMPLE)).getMessage();
    }

    @Test
    void parseValidatesFileBeforeParsingIt() {
        TestParser parser = new TestParser(validator, input -> {
            calls.add("parse");
            return List.of();
        });

        parser.parse(TestFiles.SAMPLE);

        assertEquals(List.of("validate " + TestFiles.SAMPLE, "parse"), calls);
    }

    @Test
    void parseDoesNotReadDocumentWhenValidationFails() {
        validator.failure = new MedicineParseException("XML file is not valid");
        TestParser parser = new TestParser(validator, input -> {
            calls.add("parse");
            return List.of();
        });

        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> parser.parse(TestFiles.SAMPLE));

        assertSame(validator.failure, exception);
        assertEquals(List.of("validate " + TestFiles.SAMPLE), calls);
    }

    @Test
    void parseReturnsMedicinesFromDocument() {
        TestParser parser = new TestParser(validator, input -> List.of(createMedicine()));

        assertEquals(List.of(createMedicine()), parser.parse(TestFiles.SAMPLE));
    }

    @Test
    void parseReturnsUnmodifiableCopy() {
        List<Medicine> source = new ArrayList<>(List.of(createMedicine()));
        TestParser parser = new TestParser(validator, input -> source);

        List<Medicine> result = parser.parse(TestFiles.SAMPLE);
        source.clear();

        assertEquals(1, result.size());
        assertThrows(UnsupportedOperationException.class, () -> result.add(createMedicine()));
    }

    @Test
    void parsePassesFileContentToParseDocument() {
        AtomicInteger firstByte = new AtomicInteger();
        TestParser parser = new TestParser(validator, input -> {
            firstByte.set(input.read());
            return List.of();
        });

        parser.parse(TestFiles.SAMPLE);

        assertEquals('<', firstByte.get());
    }

    @Test
    void parseReportsMissingFile() {
        Path missing = TestFiles.fixture("missing.xml");
        TestParser parser = new TestParser(validator, input -> List.of());

        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> parser.parse(missing));

        assertTrue(exception.getMessage().startsWith("Cannot read XML file: "));
    }

    @Test
    void parseReportsInputOutputFailure() {
        String message = errorOf(input -> {
            throw new IOException("disk error");
        });

        assertTrue(message.startsWith("Cannot read XML file: "));
    }

    @Test
    void parseReportsSaxFailure() {
        String message = errorOf(input -> {
            throw new SAXException("bad xml");
        });

        assertTrue(message.startsWith("Cannot parse XML file: "));
    }

    @Test
    void parseReportsParserConfigurationFailure() {
        String message = errorOf(input -> {
            throw new ParserConfigurationException("bad config");
        });

        assertTrue(message.startsWith("Cannot parse XML file: "));
    }

    @Test
    void parseReportsStreamFailure() {
        String message = errorOf(input -> {
            throw new XMLStreamException("bad stream");
        });

        assertTrue(message.startsWith("Cannot parse XML file: "));
    }

    @Test
    void parseKeepsMedicineParseExceptionFromParseDocument() {
        MedicineParseException failure = new MedicineParseException("Invalid medicine");
        TestParser parser = new TestParser(validator, input -> {
            throw failure;
        });

        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> parser.parse(TestFiles.SAMPLE));

        assertSame(failure, exception);
    }

    @Test
    void parseRejectsNullFile() {
        TestParser parser = new TestParser(validator, input -> List.of());

        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
    }

    @Test
    void constructorRejectsNullValidator() {
        assertThrows(IllegalArgumentException.class,
                () -> new TestParser(null, input -> List.of()));
    }
}