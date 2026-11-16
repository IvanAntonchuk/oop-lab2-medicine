package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.MedicineGroup;
import ua.antonchuk.medicine.model.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicineBuilderTest {

    private static MedicineBuilder validBuilder() {
        return new MedicineBuilder()
                .id("MED-001")
                .name("Amoxicillin")
                .pharm("Farmak")
                .group("antibiotic")
                .addAnalog("Ospamox")
                .addAnalog("Flemoxin Solutab")
                .addVersion(TestData.version(MedicineForm.CAPSULE))
                .addVersion(TestData.version(MedicineForm.POWDER));
    }

    private static String errorOf(MedicineBuilder builder) {
        return assertThrows(MedicineParseException.class, builder::build).getMessage();
    }

    @Test
    void buildCreatesMedicineFromTextValues() {
        Medicine expected = new Medicine("MED-001", "Amoxicillin", "Farmak",
                MedicineGroup.ANTIBIOTIC, List.of("Ospamox", "Flemoxin Solutab"),
                List.of(TestData.version(MedicineForm.CAPSULE),
                        TestData.version(MedicineForm.POWDER)));

        assertEquals(expected, validBuilder().build());
    }

    @Test
    void buildAllowsMedicineWithoutAnalogs() {
        Medicine medicine = new MedicineBuilder()
                .id("MED-001")
                .name("Amoxicillin")
                .pharm("Farmak")
                .group("antibiotic")
                .addVersion(TestData.version(MedicineForm.CAPSULE))
                .build();

        assertTrue(medicine.getAnalogs().isEmpty());
    }

    @Test
    void settersReturnSameBuilder() {
        MedicineBuilder builder = new MedicineBuilder();

        assertSame(builder, builder.id("MED-001"));
        assertSame(builder, builder.name("Amoxicillin"));
        assertSame(builder, builder.pharm("Farmak"));
        assertSame(builder, builder.group("antibiotic"));
        assertSame(builder, builder.addAnalog("Ospamox"));
        assertSame(builder, builder.addVersion(TestData.version(MedicineForm.CAPSULE)));
    }

    @Test
    void buildRejectsUnknownGroup() {
        assertEquals("Invalid medicine: Unknown MedicineGroup value: magic",
                errorOf(validBuilder().group("magic")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void buildRejectsBlankId(String id) {
        assertEquals("Invalid medicine: Id must not be null or blank",
                errorOf(validBuilder().id(id)));
    }

    @Test
    void buildRejectsMedicineWithoutVersions() {
        MedicineBuilder builder = new MedicineBuilder()
                .id("MED-001")
                .name("Amoxicillin")
                .pharm("Farmak")
                .group("antibiotic");

        assertEquals("Invalid medicine: Versions must not be empty", errorOf(builder));
    }

    @Test
    void buildRejectsDuplicateForms() {
        MedicineBuilder builder = validBuilder()
                .addVersion(TestData.version(MedicineForm.CAPSULE));

        assertEquals("Invalid medicine: Duplicate medicine form: CAPSULE", errorOf(builder));
    }
}