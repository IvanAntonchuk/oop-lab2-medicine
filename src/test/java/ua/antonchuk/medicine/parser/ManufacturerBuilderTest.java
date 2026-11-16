package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ua.antonchuk.medicine.model.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManufacturerBuilderTest {

    private static ManufacturerBuilder validBuilder() {
        return new ManufacturerBuilder()
                .name("Farmak")
                .certificate("UA/10234/01/01", "2021-03-15", "2026-03-15")
                .organization("State Expert Center of the Ministry of Health")
                .medicinePackage("blister", "16", "85.50")
                .dosage("mg", "500", "3");
    }

    private static String errorOf(ManufacturerBuilder builder) {
        return assertThrows(MedicineParseException.class, builder::build).getMessage();
    }

    @Test
    void buildCreatesManufacturerFromTextValues() {
        assertEquals(TestData.manufacturer("Farmak"), validBuilder().build());
    }

    @Test
    void settersReturnSameBuilder() {
        ManufacturerBuilder builder = new ManufacturerBuilder();

        assertSame(builder, builder.name("Farmak"));
        assertSame(builder, builder.certificate("UA/10234/01/01", "2021-03-15", "2026-03-15"));
        assertSame(builder, builder.organization("Organization"));
        assertSame(builder, builder.medicinePackage("blister", "16", "85.50"));
        assertSame(builder, builder.dosage("mg", "500", "3"));
    }

    @Test
    void buildRejectsInvalidIssueDate() {
        ManufacturerBuilder builder = validBuilder()
                .certificate("UA/10234/01/01", "15.03.2021", "2026-03-15");

        assertEquals("Invalid manufacturer: Issue date has invalid value: 15.03.2021",
                errorOf(builder));
    }

    @Test
    void buildRejectsExpirationDateNotAfterIssueDate() {
        ManufacturerBuilder builder = validBuilder()
                .certificate("UA/10234/01/01", "2026-03-15", "2021-03-15");

        assertEquals("Invalid manufacturer: Expiration date must be after issue date: "
                + "2026-03-15 - 2021-03-15", errorOf(builder));
    }

    @Test
    void buildRejectsUnknownPackageType() {
        ManufacturerBuilder builder = validBuilder().medicinePackage("jar", "16", "85.50");

        assertEquals("Invalid manufacturer: Unknown PackageKind value: jar", errorOf(builder));
    }

    @Test
    void buildRejectsInvalidQuantity() {
        ManufacturerBuilder builder = validBuilder().medicinePackage("blister", "abc", "85.50");

        assertEquals("Invalid manufacturer: Quantity has invalid value: abc", errorOf(builder));
    }

    @Test
    void buildRejectsInvalidPrice() {
        ManufacturerBuilder builder = validBuilder().medicinePackage("blister", "16", "abc");

        assertEquals("Invalid manufacturer: Price has invalid value: abc", errorOf(builder));
    }

    @Test
    void buildRejectsUnknownDosageUnit() {
        ManufacturerBuilder builder = validBuilder().dosage("kg", "500", "3");

        assertEquals("Invalid manufacturer: Unknown DosageUnit value: kg", errorOf(builder));
    }

    @Test
    void buildRejectsInvalidAmount() {
        ManufacturerBuilder builder = validBuilder().dosage("mg", "abc", "3");

        assertEquals("Invalid manufacturer: Amount has invalid value: abc", errorOf(builder));
    }

    @Test
    void buildRejectsInvalidTimesPerDay() {
        ManufacturerBuilder builder = validBuilder().dosage("mg", "500", "abc");

        assertEquals("Invalid manufacturer: Times per day has invalid value: abc",
                errorOf(builder));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void buildRejectsBlankName(String name) {
        assertEquals("Invalid manufacturer: Manufacturer name must not be null or blank",
                errorOf(validBuilder().name(name)));
    }

    @Test
    void buildRejectsMissingData() {
        assertTrue(errorOf(new ManufacturerBuilder()).startsWith("Invalid manufacturer: "));
    }

    @Test
    void buildKeepsOriginalExceptionAsCause() {
        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> validBuilder().medicinePackage("jar", "16", "85.50").build());

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }
}