package ua.antonchuk.medicine.parser;

import ua.antonchuk.medicine.model.Certificate;
import ua.antonchuk.medicine.model.Dosage;
import ua.antonchuk.medicine.model.DosageUnit;
import ua.antonchuk.medicine.model.Manufacturer;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.MedicineGroup;
import ua.antonchuk.medicine.model.MedicinePackage;
import ua.antonchuk.medicine.model.PackageKind;
import ua.antonchuk.medicine.model.TestData;
import ua.antonchuk.medicine.model.Version;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

final class ExpectedData {

    private static final String ORGANIZATION = "State Expert Center of the Ministry of Health";

    private ExpectedData() {
    }

    static Medicine amoxicillinCapsule() {
        return new Medicine("MED-001", "Amoxicillin", "Farmak", MedicineGroup.ANTIBIOTIC,
                List.of("Ospamox", "Flemoxin Solutab"),
                List.of(TestData.version(MedicineForm.CAPSULE)));
    }

    static Medicine amoxicillin() {
        Manufacturer farmakCapsule = new Manufacturer("Farmak",
                new Certificate("UA/10234/01/01", LocalDate.of(2021, 3, 15),
                        LocalDate.of(2026, 3, 15), ORGANIZATION),
                new MedicinePackage(PackageKind.BLISTER, 16, new BigDecimal("85.50")),
                new Dosage(DosageUnit.MG, 500, 3));
        Manufacturer darnitsaCapsule = new Manufacturer("Darnitsa",
                new Certificate("UA/10987/01/01", LocalDate.of(2022, 6, 1),
                        LocalDate.of(2027, 6, 1), ORGANIZATION),
                new MedicinePackage(PackageKind.BLISTER, 20, new BigDecimal("98.00")),
                new Dosage(DosageUnit.MG, 500, 3));
        Manufacturer farmakPowder = new Manufacturer("Farmak",
                new Certificate("UA/10235/02/01", LocalDate.of(2021, 3, 15),
                        LocalDate.of(2026, 3, 15), ORGANIZATION),
                new MedicinePackage(PackageKind.BOTTLE, 1, new BigDecimal("110.00")),
                new Dosage(DosageUnit.MG, 250, 3));
        return new Medicine("MED-001", "Amoxicillin", "Farmak", MedicineGroup.ANTIBIOTIC,
                List.of("Ospamox", "Flemoxin Solutab"),
                List.of(new Version(MedicineForm.CAPSULE, List.of(farmakCapsule, darnitsaCapsule)),
                        new Version(MedicineForm.POWDER, List.of(farmakPowder))));
    }
}