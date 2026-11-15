package ua.antonchuk.medicine.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicineTest {

    private static final String ID = "MED-001";
    private static final String NAME = "Amoxicillin";
    private static final String PHARM = "Farmak";
    private static final List<String> ANALOGS = List.of("Ospamox", "Flemoxin Solutab");

    private static Version version(MedicineForm form) {
        return new Version(form, List.of(TestData.manufacturer("Farmak")));
    }

    private static List<Version> createVersions() {
        return List.of(version(MedicineForm.CAPSULE),
                version(MedicineForm.POWDER));
    }

    private static Medicine createMedicine() {
        return new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS,
                createVersions());
    }

    @Test
    void constructorStoresValidValues() {
        Medicine medicine = createMedicine();

        assertEquals(ID, medicine.getId());
        assertEquals(NAME, medicine.getName());
        assertEquals(PHARM, medicine.getPharm());
        assertEquals(MedicineGroup.ANTIBIOTIC, medicine.getGroup());
        assertEquals(ANALOGS, medicine.getAnalogs());
        assertEquals(createVersions(), medicine.getVersions());
    }

    @Test
    void constructorTrimsTextFields() {
        Medicine medicine = new Medicine("  " + ID + "  ", "  " + NAME + "  ",
                "  " + PHARM + "  ", MedicineGroup.ANTIBIOTIC, ANALOGS, createVersions());

        assertEquals(ID, medicine.getId());
        assertEquals(NAME, medicine.getName());
        assertEquals(PHARM, medicine.getPharm());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankId(String id) {
        assertThrows(IllegalArgumentException.class,
                () -> new Medicine(id, NAME, PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS,
                        createVersions()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class,
                () -> new Medicine(ID, name, PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS,
                        createVersions()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void constructorRejectsBlankPharm(String pharm) {
        assertThrows(IllegalArgumentException.class,
                () -> new Medicine(ID, NAME, pharm, MedicineGroup.ANTIBIOTIC, ANALOGS,
                        createVersions()));
    }

    @Test
    void constructorRejectsNullGroup() {
        assertThrows(IllegalArgumentException.class,
                () -> new Medicine(ID, NAME, PHARM, null, ANALOGS, createVersions()));
    }

    @Test
    void constructorAllowsEmptyAnalogs() {
        Medicine medicine = new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC,
                List.of(), createVersions());

        assertTrue(medicine.getAnalogs().isEmpty());
    }

    @Test
    void constructorRejectsNullAnalogs() {
        assertThrows(IllegalArgumentException.class,
                () -> new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC, null,
                        createVersions()));
    }

    @Test
    void constructorRejectsNullVersions() {
        assertThrows(IllegalArgumentException.class,
                () -> new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS, null));
    }

    @Test
    void constructorRejectsEmptyVersions() {
        assertThrows(IllegalArgumentException.class,
                () -> new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS,
                        List.of()));
    }

    @Test
    void constructorRejectsDuplicateMedicineForms() {
        List<Version> versions = List.of(version(MedicineForm.CAPSULE),
                version(MedicineForm.CAPSULE));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS,
                        versions));

        assertEquals("Duplicate medicine form: CAPSULE", exception.getMessage());
    }

    @Test
    void analogsAndVersionsAreCopiedAndUnmodifiable() {
        List<String> analogs = new ArrayList<>(ANALOGS);
        List<Version> versions = new ArrayList<>(createVersions());

        Medicine medicine = new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC, analogs,
                versions);
        analogs.add("Another");
        versions.add(version(MedicineForm.TABLET));

        assertEquals(ANALOGS, medicine.getAnalogs());
        assertEquals(createVersions(), medicine.getVersions());
        assertThrows(UnsupportedOperationException.class,
                () -> medicine.getAnalogs().add("Another"));
        assertThrows(UnsupportedOperationException.class,
                () -> medicine.getVersions().add(version(MedicineForm.TABLET)));
    }

    @Test
    void equalsAndHashCodeMatchForSameValues() {
        Medicine first = createMedicine();
        Medicine second = createMedicine();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsIsReflexive() {
        Medicine medicine = createMedicine();

        assertTrue(medicine.equals(medicine));
    }

    @Test
    void equalsRejectsNullAndOtherTypes() {
        Medicine medicine = createMedicine();

        assertNotEquals(medicine, null);
        assertNotEquals(medicine, "medicine");
    }

    @Test
    void equalsDetectsDifferentId() {
        assertNotEquals(createMedicine(),
                new Medicine("MED-002", NAME, PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS,
                        createVersions()));
    }

    @Test
    void equalsDetectsDifferentName() {
        assertNotEquals(createMedicine(),
                new Medicine(ID, "Azithromycin", PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS,
                        createVersions()));
    }

    @Test
    void equalsDetectsDifferentPharm() {
        assertNotEquals(createMedicine(),
                new Medicine(ID, NAME, "Darnitsa", MedicineGroup.ANTIBIOTIC, ANALOGS,
                        createVersions()));
    }

    @Test
    void equalsDetectsDifferentGroup() {
        assertNotEquals(createMedicine(),
                new Medicine(ID, NAME, PHARM, MedicineGroup.VITAMIN, ANALOGS,
                        createVersions()));
    }

    @Test
    void equalsDetectsDifferentAnalogs() {
        assertNotEquals(createMedicine(),
                new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC, List.of("Ospamox"),
                        createVersions()));
    }

    @Test
    void equalsDetectsDifferentVersions() {
        assertNotEquals(createMedicine(),
                new Medicine(ID, NAME, PHARM, MedicineGroup.ANTIBIOTIC, ANALOGS,
                        List.of(version(MedicineForm.CAPSULE))));
    }

    @Test
    void toStringContainsAllFields() {
        String expected = "Medicine [id=MED-001, name=Amoxicillin, pharm=Farmak, "
                + "group=ANTIBIOTIC, analogs=[Ospamox, Flemoxin Solutab], versions="
                + createVersions() + "]";

        assertEquals(expected, createMedicine().toString());
    }
}