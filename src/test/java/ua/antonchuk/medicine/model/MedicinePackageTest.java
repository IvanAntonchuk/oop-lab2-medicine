package ua.antonchuk.medicine.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicinePackageTest {

    private static final BigDecimal PRICE = new BigDecimal("85.50");

    private static MedicinePackage createPackage() {
        return new MedicinePackage(PackageKind.BLISTER, 16, PRICE);
    }

    @Test
    void constructorStoresValidValues() {
        MedicinePackage medicinePackage = createPackage();

        assertEquals(PackageKind.BLISTER, medicinePackage.getType());
        assertEquals(16, medicinePackage.getQuantity());
        assertEquals(PRICE, medicinePackage.getPrice());
    }

    @Test
    void constructorRejectsNullType() {
        assertThrows(IllegalArgumentException.class,
                () -> new MedicinePackage(null, 16, PRICE));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -16})
    void constructorRejectsNonPositiveQuantity(int quantity) {
        assertThrows(IllegalArgumentException.class,
                () -> new MedicinePackage(PackageKind.BLISTER, quantity, PRICE));
    }

    @Test
    void constructorRejectsNullPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new MedicinePackage(PackageKind.BLISTER, 16, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "-1", "-85.50"})
    void constructorRejectsNonPositivePrice(String price) {
        assertThrows(IllegalArgumentException.class,
                () -> new MedicinePackage(PackageKind.BLISTER, 16, new BigDecimal(price)));
    }

    @Test
    void equalsAndHashCodeMatchForSameValues() {
        MedicinePackage first = createPackage();
        MedicinePackage second = createPackage();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsAndHashCodeIgnorePriceScale() {
        MedicinePackage first = new MedicinePackage(PackageKind.BOX, 1, new BigDecimal("100"));
        MedicinePackage second = new MedicinePackage(PackageKind.BOX, 1, new BigDecimal("100.00"));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsIsReflexive() {
        MedicinePackage medicinePackage = createPackage();

        assertTrue(medicinePackage.equals(medicinePackage));
    }

    @Test
    void equalsRejectsNullAndOtherTypes() {
        MedicinePackage medicinePackage = createPackage();

        assertNotEquals(medicinePackage, null);
        assertNotEquals(medicinePackage, "package");
    }

    @Test
    void equalsDetectsDifferentType() {
        assertNotEquals(createPackage(), new MedicinePackage(PackageKind.BOX, 16, PRICE));
    }

    @Test
    void equalsDetectsDifferentQuantity() {
        assertNotEquals(createPackage(), new MedicinePackage(PackageKind.BLISTER, 20, PRICE));
    }

    @Test
    void equalsDetectsDifferentPrice() {
        assertNotEquals(createPackage(),
                new MedicinePackage(PackageKind.BLISTER, 16, new BigDecimal("86.00")));
    }

    @Test
    void toStringContainsAllFields() {
        assertEquals("MedicinePackage [type=BLISTER, quantity=16, price=85.50]",
                createPackage().toString());
    }
}