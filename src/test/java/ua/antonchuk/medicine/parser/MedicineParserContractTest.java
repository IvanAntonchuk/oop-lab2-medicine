package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;
import ua.antonchuk.medicine.model.Medicine;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class MedicineParserContractTest {

    protected abstract MedicineParser createParser(XmlValidator validator);

    private MedicineParser parser() {
        return createParser(new XmlValidator(TestFiles.SCHEMA));
    }

    private String errorOf(Path file) {
        return assertThrows(MedicineParseException.class, () -> parser().parse(file))
                .getMessage();
    }

    @Test
    public void parseReadsSampleDocumentWithAllNestedData() {
        List<Medicine> medicines = parser().parse(TestFiles.SAMPLE);

        assertEquals(List.of("MED-001", "MED-002", "MED-003", "MED-004", "MED-005", "MED-006"),
                medicines.stream().map(Medicine::getId).toList());
        assertEquals(ExpectedData.amoxicillin(), medicines.get(0));
    }

    @Test
    public void parseIgnoresCommentsAndMergesCdataIntoText() {
        List<Medicine> medicines = parser().parse(TestFiles.fixture("comment-and-cdata.xml"));

        assertEquals(List.of(ExpectedData.amoxicillinCapsule()), medicines);
    }

    @Test
    public void parseRejectsDocumentThatViolatesSchema() {
        assertTrue(errorOf(TestFiles.fixture("invalid-group.xml"))
                .startsWith("XML file is not valid: "));
    }

    @Test
    public void parseRejectsMalformedDocument() {
        assertTrue(errorOf(TestFiles.fixture("malformed.xml"))
                .startsWith("XML file is not valid: "));
    }

    @Test
    public void parseRejectsDocumentWithEntityDeclaration() {
        assertTrue(errorOf(TestFiles.fixture("entity.xml"))
                .startsWith("Cannot parse XML file: "));
    }

    @Test
    public void parseReportsDataThatBreaksModelRules() {
        assertEquals("Invalid manufacturer: Expiration date must be after issue date: "
                        + "2026-03-15 - 2021-03-15",
                errorOf(TestFiles.fixture("certificate-dates.xml")));
    }

    @Test
    public void parseRejectsMissingFile() {
        assertTrue(errorOf(TestFiles.fixture("missing.xml"))
                .startsWith("Cannot read XML file: "));
    }

    @Test
    public void parseRejectsNullFile() {
        MedicineParser parser = parser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
    }
}