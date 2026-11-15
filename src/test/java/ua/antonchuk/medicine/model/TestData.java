package ua.antonchuk.medicine.model;

import java.math.BigDecimal;
import java.time.LocalDate;

final class TestData {

    private TestData() {
    }

    static Certificate certificate() {
        return new Certificate("UA/10234/01/01", LocalDate.of(2021, 3, 15),
                LocalDate.of(2026, 3, 15), "State Expert Center of the Ministry of Health");
    }

    static MedicinePackage medicinePackage() {
        return new MedicinePackage(PackageKind.BLISTER, 16, new BigDecimal("85.50"));
    }

    static Dosage dosage() {
        return new Dosage(DosageUnit.MG, 500, 3);
    }

    static Manufacturer manufacturer(String name) {
        return new Manufacturer(name, certificate(), medicinePackage(), dosage());
    }
}