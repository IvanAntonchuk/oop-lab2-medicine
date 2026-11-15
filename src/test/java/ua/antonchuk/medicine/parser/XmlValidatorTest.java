package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlValidatorTest {

    private final XmlValidator validator = new XmlValidator(TestFiles.SCHEMA);

    @Test
    void validateAcceptsSampleDocument() {
        assertDoesNotThrow(() -> validator.validate(TestFiles.SAMPLE));
    }

    @Test
    void validateRejectsDocumentThatViolatesSchema() {
        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> validator.validate(TestFiles.fixture("invalid-group.xml")));

        assertTrue(exception.getMessage().startsWith("XML file is not valid: "));
        assertTrue(exception.getMessage().contains("invalid-group.xml"));
    }

    @Test
    void validateRejectsMalformedDocument() {
        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> validator.validate(TestFiles.fixture("malformed.xml")));

        assertTrue(exception.getMessage().startsWith("XML file is not valid: "));
    }

    @Test
    void validateRejectsMissingFile() {
        Path missing = TestFiles.fixture("missing.xml");

        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> validator.validate(missing));

        assertTrue(exception.getMessage().startsWith("Cannot read XML file: "));
    }

    @Test
    void validateRejectsNullFile() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
    }

    @Test
    void constructorRejectsNullSchemaFile() {
        assertThrows(IllegalArgumentException.class, () -> new XmlValidator(null));
    }

    @Test
    void constructorRejectsMissingSchemaFile() {
        Path missing = Path.of("data", "missing.xsd");

        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> new XmlValidator(missing));

        assertTrue(exception.getMessage().startsWith("Invalid XSD schema: "));
    }

    @Test
    void constructorRejectsFileThatIsNotSchema() {
        Path notSchema = TestFiles.fixture("malformed.xml");

        assertThrows(MedicineParseException.class, () -> new XmlValidator(notSchema));
    }
}