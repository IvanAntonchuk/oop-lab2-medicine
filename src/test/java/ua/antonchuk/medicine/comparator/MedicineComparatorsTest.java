package ua.antonchuk.medicine.comparator;

import org.junit.jupiter.api.Test;
import ua.antonchuk.medicine.model.Manufacturer;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.MedicineGroup;
import ua.antonchuk.medicine.model.MedicinePackage;
import ua.antonchuk.medicine.model.PackageKind;
import ua.antonchuk.medicine.model.TestData;
import ua.antonchuk.medicine.model.Version;
import ua.antonchuk.medicine.parser.XmlValidator;
import ua.antonchuk.medicine.parser.stax.StaxMedicineParser;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MedicineComparatorsTest {

    private static final Path SCHEMA = Path.of("data", "medicines.xsd");
    private static final Path SAMPLE = Path.of("data", "medicines.xml");

    private static Version version(MedicineForm form, String... prices) {
        List<Manufacturer> manufacturers = new ArrayList<>();
        for (int i = 0; i < prices.length; i++) {
            manufacturers.add(new Manufacturer("Maker" + i, TestData.certificate(),
                    new MedicinePackage(PackageKind.BOX, 1, new BigDecimal(prices[i])),
                    TestData.dosage()));
        }
        return new Version(form, manufacturers);
    }

    private static Medicine medicine(String name, MedicineGroup group, Version... versions) {
        return new Medicine("MED-" + Math.abs(name.hashCode() % 1000), name, "Farmak", group,
                List.of(), List.of(versions));
    }

    private static Medicine medicine(String name, MedicineGroup group, String... prices) {
        return medicine(name, group, version(MedicineForm.TABLET, prices));
    }

    private static List<String> namesAfterSorting(List<Medicine> medicines,
                                                  Comparator<Medicine> comparator) {
        return medicines.stream().sorted(comparator).map(Medicine::getName).toList();
    }

    @Test
    void byNameSortsAlphabeticallyIgnoringCase() {
        List<Medicine> medicines = List.of(
                medicine("paracetamol", MedicineGroup.ANALGESIC, "10"),
                medicine("Amoxicillin", MedicineGroup.ANTIBIOTIC, "10"),
                medicine("ibuprofen", MedicineGroup.ANALGESIC, "10"));

        assertEquals(List.of("Amoxicillin", "ibuprofen", "paracetamol"),
                namesAfterSorting(medicines, MedicineComparators.BY_NAME));
    }

    @Test
    void byNameTreatsSameNamesWithDifferentCaseAsEqual() {
        Medicine lower = medicine("ibuprofen", MedicineGroup.ANALGESIC, "10");
        Medicine upper = medicine("IBUPROFEN", MedicineGroup.ANALGESIC, "20");

        assertEquals(0, MedicineComparators.BY_NAME.compare(lower, upper));
    }

    @Test
    void byGroupThenNameSortsByGroupAndKeepsNameOrderInsideGroup() {
        List<Medicine> medicines = List.of(
                medicine("Vitamin C", MedicineGroup.VITAMIN, "10"),
                medicine("Paracetamol", MedicineGroup.ANALGESIC, "10"),
                medicine("Azithromycin", MedicineGroup.ANTIBIOTIC, "10"),
                medicine("Ibuprofen", MedicineGroup.ANALGESIC, "10"),
                medicine("Amoxicillin", MedicineGroup.ANTIBIOTIC, "10"));

        assertEquals(List.of("Amoxicillin", "Azithromycin", "Ibuprofen", "Paracetamol",
                        "Vitamin C"),
                namesAfterSorting(medicines, MedicineComparators.BY_GROUP_THEN_NAME));
    }

    @Test
    void byCheapestPriceUsesCheapestPackageAcrossVersionsAndManufacturers() {
        Medicine expensive = medicine("Expensive", MedicineGroup.ANTIBIOTIC, "90", "80");
        Medicine twoVersions = medicine("TwoVersions", MedicineGroup.ANTIBIOTIC,
                version(MedicineForm.TABLET, "50", "70"), version(MedicineForm.CAPSULE, "20"));
        Medicine cheap = medicine("Cheap", MedicineGroup.ANTIBIOTIC, "5", "300");

        assertEquals(List.of("Cheap", "TwoVersions", "Expensive"),
                namesAfterSorting(List.of(expensive, twoVersions, cheap),
                        MedicineComparators.BY_CHEAPEST_PRICE));
    }

    @Test
    void byCheapestPriceComparesNumericallyAndBreaksTiesByName() {
        Medicine beta = medicine("Beta", MedicineGroup.VITAMIN, "10");
        Medicine alpha = medicine("Alpha", MedicineGroup.VITAMIN, "10.00");

        assertEquals(List.of("Alpha", "Beta"),
                namesAfterSorting(List.of(beta, alpha), MedicineComparators.BY_CHEAPEST_PRICE));
    }

    @Test
    void comparatorsOrderSampleDocumentAsExpected() {
        List<Medicine> medicines =
                new StaxMedicineParser(new XmlValidator(SCHEMA)).parse(SAMPLE);

        assertEquals(List.of("Amoxicillin", "Ascorbic Acid", "Azithromycin", "Cholecalciferol",
                        "Ibuprofen", "Paracetamol"),
                namesAfterSorting(medicines, MedicineComparators.BY_NAME));
        assertEquals(List.of("Amoxicillin", "Azithromycin", "Ibuprofen", "Paracetamol",
                        "Ascorbic Acid", "Cholecalciferol"),
                namesAfterSorting(medicines, MedicineComparators.BY_GROUP_THEN_NAME));
        assertEquals(List.of("Ascorbic Acid", "Paracetamol", "Ibuprofen", "Amoxicillin",
                        "Cholecalciferol", "Azithromycin"),
                namesAfterSorting(medicines, MedicineComparators.BY_CHEAPEST_PRICE));
    }
}