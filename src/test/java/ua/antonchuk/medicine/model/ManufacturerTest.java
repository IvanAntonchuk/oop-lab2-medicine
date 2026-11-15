package ua.antonchuk.medicine.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManufacturerTest {

    private static final String NAME = "Farmak";

    private static Manufacturer createManufacturer() {
        return new Manufacturer(NAME, TestData.certificate(), TestData.medicinePackage(),
                TestData.dosage());
    }

    @Test
    void constructorStoresValidValues() {
        Manufacturer manufacturer = createManufacturer();

        assertEquals(NAME, manufacturer.getName());
        assertEquals(TestData.certificate(), manufacturer.getCertificate());
        assertEquals(TestData.medicinePackage(), manufacturer.getMedicinePackage());
        assertEquals(TestData.dosage(), manufacturer.getDosage());
    }

    @Test
    void constructorTrimsName() {
        assertEquals(NAME, TestData.manufacturer("  " + NAME + "  ").getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> TestData.manufacturer(name));
    }

    @Test
    void constructorRejectsNullCertificate() {
        assertThrows(IllegalArgumentException.class,
                () -> new Manufacturer(NAME, null, TestData.medicinePackage(),
                        TestData.dosage()));
    }

    @Test
    void constructorRejectsNullPackage() {
        assertThrows(IllegalArgumentException.class,
                () -> new Manufacturer(NAME, TestData.certificate(), null, TestData.dosage()));
    }

    @Test
    void constructorRejectsNullDosage() {
        assertThrows(IllegalArgumentException.class,
                () -> new Manufacturer(NAME, TestData.certificate(),
                        TestData.medicinePackage(), null));
    }

    @Test
    void equalsAndHashCodeMatchForSameValues() {
        Manufacturer first = createManufacturer();
        Manufacturer second = createManufacturer();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsIsReflexive() {
        Manufacturer manufacturer = createManufacturer();

        assertTrue(manufacturer.equals(manufacturer));
    }

    @Test
    void equalsRejectsNullAndOtherTypes() {
        Manufacturer manufacturer = createManufacturer();

        assertNotEquals(manufacturer, null);
        assertNotEquals(manufacturer, "manufacturer");
    }

    @Test
    void equalsDetectsDifferentName() {
        assertNotEquals(createManufacturer(), TestData.manufacturer("Darnitsa"));
    }

    @Test
    void equalsDetectsDifferentCertificate() {
        Certificate other = new Certificate("UA/99999/01/01",
                TestData.certificate().getIssueDate(),
                TestData.certificate().getExpirationDate(),
                TestData.certificate().getOrganization());

        assertNotEquals(createManufacturer(),
                new Manufacturer(NAME, other, TestData.medicinePackage(), TestData.dosage()));
    }

    @Test
    void equalsDetectsDifferentPackage() {
        MedicinePackage other = new MedicinePackage(PackageKind.BOX, 16,
                TestData.medicinePackage().getPrice());

        assertNotEquals(createManufacturer(),
                new Manufacturer(NAME, TestData.certificate(), other, TestData.dosage()));
    }

    @Test
    void equalsDetectsDifferentDosage() {
        Dosage other = new Dosage(DosageUnit.G, 500, 3);

        assertNotEquals(createManufacturer(),
                new Manufacturer(NAME, TestData.certificate(), TestData.medicinePackage(),
                        other));
    }

    @Test
    void toStringContainsAllFields() {
        String expected = "Manufacturer [name=Farmak, certificate=" + TestData.certificate()
                + ", package=" + TestData.medicinePackage()
                + ", dosage=" + TestData.dosage() + "]";

        assertEquals(expected, createManufacturer().toString());
    }
}