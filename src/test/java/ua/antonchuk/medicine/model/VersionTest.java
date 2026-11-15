package ua.antonchuk.medicine.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionTest {

    private static Version createVersion() {
        return new Version(MedicineForm.CAPSULE,
                List.of(TestData.manufacturer("Farmak"), TestData.manufacturer("Darnitsa")));
    }

    @Test
    void constructorStoresValidValues() {
        Version version = createVersion();

        assertEquals(MedicineForm.CAPSULE, version.getForm());
        assertEquals(List.of(TestData.manufacturer("Farmak"), TestData.manufacturer("Darnitsa")),
                version.getManufacturers());
    }

    @Test
    void constructorRejectsNullForm() {
        assertThrows(IllegalArgumentException.class,
                () -> new Version(null, List.of(TestData.manufacturer("Farmak"))));
    }

    @Test
    void constructorRejectsNullManufacturers() {
        assertThrows(IllegalArgumentException.class,
                () -> new Version(MedicineForm.CAPSULE, null));
    }

    @Test
    void constructorRejectsEmptyManufacturers() {
        assertThrows(IllegalArgumentException.class,
                () -> new Version(MedicineForm.CAPSULE, List.of()));
    }

    @Test
    void manufacturersAreCopiedAndUnmodifiable() {
        List<Manufacturer> source = new ArrayList<>(List.of(TestData.manufacturer("Farmak")));

        Version version = new Version(MedicineForm.CAPSULE, source);
        source.add(TestData.manufacturer("Darnitsa"));

        assertEquals(1, version.getManufacturers().size());
        assertThrows(UnsupportedOperationException.class,
                () -> version.getManufacturers().add(TestData.manufacturer("Darnitsa")));
    }

    @Test
    void equalsAndHashCodeMatchForSameValues() {
        Version first = createVersion();
        Version second = createVersion();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsIsReflexive() {
        Version version = createVersion();

        assertTrue(version.equals(version));
    }

    @Test
    void equalsRejectsNullAndOtherTypes() {
        Version version = createVersion();

        assertNotEquals(version, null);
        assertNotEquals(version, "version");
    }

    @Test
    void equalsDetectsDifferentForm() {
        Version other = new Version(MedicineForm.TABLET,
                List.of(TestData.manufacturer("Farmak"), TestData.manufacturer("Darnitsa")));

        assertNotEquals(createVersion(), other);
    }

    @Test
    void equalsDetectsDifferentManufacturers() {
        Version other = new Version(MedicineForm.CAPSULE,
                List.of(TestData.manufacturer("Farmak")));

        assertNotEquals(createVersion(), other);
    }

    @Test
    void toStringContainsAllFields() {
        String expected = "Version [form=CAPSULE, manufacturers=["
                + TestData.manufacturer("Farmak") + ", "
                + TestData.manufacturer("Darnitsa") + "]]";

        assertEquals(expected, createVersion().toString());
    }
}