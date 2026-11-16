package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.TestData;
import ua.antonchuk.medicine.model.Version;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VersionBuilderTest {

    private static String errorOf(VersionBuilder builder) {
        return assertThrows(MedicineParseException.class, builder::build).getMessage();
    }

    @Test
    void buildCreatesVersionFromFormAndManufacturers() {
        Version version = new VersionBuilder()
                .form("capsule")
                .addManufacturer(TestData.manufacturer("Farmak"))
                .addManufacturer(TestData.manufacturer("Darnitsa"))
                .build();

        assertEquals(MedicineForm.CAPSULE, version.getForm());
        assertEquals(List.of(TestData.manufacturer("Farmak"), TestData.manufacturer("Darnitsa")),
                version.getManufacturers());
    }

    @Test
    void settersReturnSameBuilder() {
        VersionBuilder builder = new VersionBuilder();

        assertSame(builder, builder.form("capsule"));
        assertSame(builder, builder.addManufacturer(TestData.manufacturer("Farmak")));
    }

    @Test
    void buildRejectsUnknownForm() {
        VersionBuilder builder = new VersionBuilder()
                .form("jelly")
                .addManufacturer(TestData.manufacturer("Farmak"));

        assertEquals("Invalid version: Unknown MedicineForm value: jelly", errorOf(builder));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void buildRejectsBlankForm(String form) {
        VersionBuilder builder = new VersionBuilder()
                .form(form)
                .addManufacturer(TestData.manufacturer("Farmak"));

        assertEquals("Invalid version: MedicineForm value must not be null or blank",
                errorOf(builder));
    }

    @Test
    void buildRejectsVersionWithoutManufacturers() {
        VersionBuilder builder = new VersionBuilder().form("capsule");

        assertEquals("Invalid version: Manufacturers must not be empty", errorOf(builder));
    }
}