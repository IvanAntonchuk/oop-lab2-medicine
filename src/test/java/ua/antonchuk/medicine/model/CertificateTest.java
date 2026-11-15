package ua.antonchuk.medicine.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateTest {

    private static final String NUMBER = "UA/10234/01/01";
    private static final LocalDate ISSUED = LocalDate.of(2021, 3, 15);
    private static final LocalDate EXPIRES = LocalDate.of(2026, 3, 15);
    private static final String ORGANIZATION = "State Expert Center of the Ministry of Health";

    private static Certificate createCertificate() {
        return new Certificate(NUMBER, ISSUED, EXPIRES, ORGANIZATION);
    }

    @Test
    void constructorStoresValidValues() {
        Certificate certificate = createCertificate();

        assertEquals(NUMBER, certificate.getNumber());
        assertEquals(ISSUED, certificate.getIssueDate());
        assertEquals(EXPIRES, certificate.getExpirationDate());
        assertEquals(ORGANIZATION, certificate.getOrganization());
    }

    @Test
    void constructorTrimsTextFields() {
        Certificate certificate = new Certificate("  " + NUMBER + "  ", ISSUED, EXPIRES,
                "  " + ORGANIZATION + "  ");

        assertEquals(NUMBER, certificate.getNumber());
        assertEquals(ORGANIZATION, certificate.getOrganization());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankNumber(String number) {
        assertThrows(IllegalArgumentException.class,
                () -> new Certificate(number, ISSUED, EXPIRES, ORGANIZATION));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankOrganization(String organization) {
        assertThrows(IllegalArgumentException.class,
                () -> new Certificate(NUMBER, ISSUED, EXPIRES, organization));
    }

    @Test
    void constructorRejectsNullIssueDate() {
        assertThrows(IllegalArgumentException.class,
                () -> new Certificate(NUMBER, null, EXPIRES, ORGANIZATION));
    }

    @Test
    void constructorRejectsNullExpirationDate() {
        assertThrows(IllegalArgumentException.class,
                () -> new Certificate(NUMBER, ISSUED, null, ORGANIZATION));
    }

    @Test
    void constructorRejectsExpirationDateEqualToIssueDate() {
        assertThrows(IllegalArgumentException.class,
                () -> new Certificate(NUMBER, ISSUED, ISSUED, ORGANIZATION));
    }

    @Test
    void constructorRejectsExpirationDateBeforeIssueDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Certificate(NUMBER, EXPIRES, ISSUED, ORGANIZATION));

        assertEquals("Expiration date must be after issue date: 2026-03-15 - 2021-03-15",
                exception.getMessage());
    }

    @Test
    void equalsAndHashCodeMatchForSameValues() {
        Certificate first = createCertificate();
        Certificate second = createCertificate();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsIsReflexive() {
        Certificate certificate = createCertificate();

        assertTrue(certificate.equals(certificate));
    }

    @Test
    void equalsRejectsNullAndOtherTypes() {
        Certificate certificate = createCertificate();

        assertNotEquals(certificate, null);
        assertNotEquals(certificate, "certificate");
    }

    @Test
    void equalsDetectsDifferentNumber() {
        Certificate other = new Certificate("UA/99999/01/01", ISSUED, EXPIRES, ORGANIZATION);

        assertNotEquals(createCertificate(), other);
    }

    @Test
    void equalsDetectsDifferentIssueDate() {
        Certificate other = new Certificate(NUMBER, ISSUED.minusDays(1), EXPIRES, ORGANIZATION);

        assertNotEquals(createCertificate(), other);
    }

    @Test
    void equalsDetectsDifferentExpirationDate() {
        Certificate other = new Certificate(NUMBER, ISSUED, EXPIRES.plusYears(1), ORGANIZATION);

        assertNotEquals(createCertificate(), other);
    }

    @Test
    void equalsDetectsDifferentOrganization() {
        Certificate other = new Certificate(NUMBER, ISSUED, EXPIRES, "Another Organization");

        assertNotEquals(createCertificate(), other);
    }

    @Test
    void toStringContainsAllFields() {
        assertEquals("Certificate [number=UA/10234/01/01, issued=2021-03-15, "
                        + "expires=2026-03-15, "
                        + "organization=State Expert Center of the Ministry of Health]",
                createCertificate().toString());
    }
}