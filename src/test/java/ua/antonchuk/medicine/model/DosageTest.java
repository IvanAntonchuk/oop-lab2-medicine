package ua.antonchuk.medicine.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DosageTest {

    private static final double DELTA = 1e-9;

    private static Dosage createDosage() {
        return new Dosage(DosageUnit.MG, 500, 3);
    }

    @Test
    void constructorStoresValidValues() {
        Dosage dosage = createDosage();

        assertEquals(DosageUnit.MG, dosage.getUnit());
        assertEquals(500.0, dosage.getAmount(), DELTA);
        assertEquals(3, dosage.getTimesPerDay());
    }

    @Test
    void constructorRejectsNullUnit() {
        assertThrows(IllegalArgumentException.class, () -> new Dosage(null, 500, 3));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY})
    void constructorRejectsInvalidAmount(double amount) {
        assertThrows(IllegalArgumentException.class,
                () -> new Dosage(DosageUnit.MG, amount, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -3})
    void constructorRejectsNonPositiveTimesPerDay(int timesPerDay) {
        assertThrows(IllegalArgumentException.class,
                () -> new Dosage(DosageUnit.MG, 500, timesPerDay));
    }

    @Test
    void equalsAndHashCodeMatchForSameValues() {
        Dosage first = createDosage();
        Dosage second = createDosage();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsIsReflexive() {
        Dosage dosage = createDosage();

        assertTrue(dosage.equals(dosage));
    }

    @Test
    void equalsRejectsNullAndOtherTypes() {
        Dosage dosage = createDosage();

        assertNotEquals(dosage, null);
        assertNotEquals(dosage, "dosage");
    }

    @Test
    void equalsDetectsDifferentUnit() {
        assertNotEquals(createDosage(), new Dosage(DosageUnit.G, 500, 3));
    }

    @Test
    void equalsDetectsDifferentAmount() {
        assertNotEquals(createDosage(), new Dosage(DosageUnit.MG, 250, 3));
    }

    @Test
    void equalsDetectsDifferentTimesPerDay() {
        assertNotEquals(createDosage(), new Dosage(DosageUnit.MG, 500, 4));
    }

    @Test
    void toStringContainsAllFields() {
        assertEquals("Dosage [amount=500.0 MG, timesPerDay=3]", createDosage().toString());
    }
}