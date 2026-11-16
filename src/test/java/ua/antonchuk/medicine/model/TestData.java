package ua.antonchuk.medicine.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class TestData {

    private TestData() {
    }

    public static Certificate certificate() {
        return new Certificate("UA/10234/01/01", LocalDate.of(2021, 3, 15),
                LocalDate.of(2026, 3, 15), "State Expert Center of the Ministry of Health");
    }

    public static MedicinePackage medicinePackage() {
        return new MedicinePackage(PackageKind.BLISTER, 16, new BigDecimal("85.50"));
    }

    public static Dosage dosage() {
        return new Dosage(DosageUnit.MG, 500, 3);
    }

    public static Manufacturer manufacturer(String name) {
        return new Manufacturer(name, certificate(), medicinePackage(), dosage());
    }

    public static Version version(MedicineForm form) {
        return new Version(form, List.of(manufacturer("Farmak")));
    }
}