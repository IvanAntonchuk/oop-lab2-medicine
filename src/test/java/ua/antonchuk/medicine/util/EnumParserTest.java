package ua.antonchuk.medicine.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ua.antonchuk.medicine.model.DosageUnit;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.MedicineGroup;
import ua.antonchuk.medicine.model.PackageKind;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumParserTest {

    @ParameterizedTest
    @ValueSource(strings = {"antibiotic", "ANTIBIOTIC", "Antibiotic", "  antibiotic  "})
    void parseIgnoresCaseAndSurroundingSpaces(String value) {
        assertEquals(MedicineGroup.ANTIBIOTIC, EnumParser.parse(MedicineGroup.class, value));
    }

    @Test
    void parseCoversAllMedicineGroupValuesFromSchema() {
        assertCoversSchemaValues(MedicineGroup.class,
                "antibiotic", "analgesic", "vitamin", "antiviral", "antiseptic", "antihistamine");
    }

    @Test
    void parseCoversAllMedicineFormValuesFromSchema() {
        assertCoversSchemaValues(MedicineForm.class,
                "tablet", "capsule", "powder", "drops", "syrup", "ointment");
    }

    @Test
    void parseCoversAllPackageKindValuesFromSchema() {
        assertCoversSchemaValues(PackageKind.class,
                "blister", "bottle", "box", "tube", "sachet", "ampoule");
    }

    @Test
    void parseCoversAllDosageUnitValuesFromSchema() {
        assertCoversSchemaValues(DosageUnit.class, "mcg", "mg", "g", "ml");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void parseRejectsNullOrBlankValue(String value) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> EnumParser.parse(MedicineGroup.class, value));

        assertEquals("MedicineGroup value must not be null or blank", exception.getMessage());
    }

    @Test
    void parseRejectsUnknownValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> EnumParser.parse(MedicineGroup.class, "magic"));

        assertEquals("Unknown MedicineGroup value: magic", exception.getMessage());
    }

    @Test
    void parseRejectsNullEnumType() {
        assertThrows(IllegalArgumentException.class,
                () -> EnumParser.<MedicineGroup>parse(null, "antibiotic"));
    }

    private static <E extends Enum<E>> void assertCoversSchemaValues(
            Class<E> enumType, String... schemaValues) {
        assertEquals(schemaValues.length, enumType.getEnumConstants().length);
        for (String schemaValue : schemaValues) {
            E parsed = EnumParser.parse(enumType, schemaValue);
            assertEquals(schemaValue, parsed.name().toLowerCase(Locale.ROOT));
        }
    }
}