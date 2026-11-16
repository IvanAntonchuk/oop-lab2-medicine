package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.MedicineGroup;
import ua.antonchuk.medicine.model.TestData;
import ua.antonchuk.medicine.model.Version;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicineEventHandlerTest {

    private final MedicineEventHandler handler = new MedicineEventHandler();

    private void start(String name, String... attributes) {
        Map<String, String> values = new HashMap<>();
        for (int i = 0; i < attributes.length; i += 2) {
            values.put(attributes[i], attributes[i + 1]);
        }
        handler.startElement(name, values::get);
    }

    private void end(String name) {
        handler.endElement(name);
    }

    private void text(String name, String value) {
        int middle = value.length() / 2;
        start(name);
        handler.characters("\n        ");
        handler.characters(value.substring(0, middle));
        handler.characters(value.substring(middle));
        handler.characters("\n    ");
        end(name);
    }

    private void manufacturer(String name, String packageType) {
        start("Manufacturer", "name", name);
        start("Certificate", "number", "UA/10234/01/01", "issued", "2021-03-15",
                "expires", "2026-03-15");
        text("Organization", "State Expert Center of the Ministry of Health");
        end("Certificate");
        start("Package", "type", packageType, "quantity", "16", "price", "85.50");
        end("Package");
        start("Dosage", "unit", "mg", "amount", "500", "timesPerDay", "3");
        end("Dosage");
        end("Manufacturer");
    }

    private void version(String form, String... manufacturerNames) {
        start("Version", "form", form);
        for (String manufacturerName : manufacturerNames) {
            manufacturer(manufacturerName, "blister");
        }
        end("Version");
    }

    private void startMedicine(String id, String name, String group) {
        start("Medicine", "id", id);
        text("Name", name);
        text("Pharm", "Farmak");
        text("Group", group);
        start("Analogs");
        text("Analog", "Ospamox");
        text("Analog", "Flemoxin Solutab");
        end("Analogs");
        start("Versions");
    }

    private void endMedicine() {
        end("Versions");
        end("Medicine");
    }

    private static Medicine expectedMedicine(String id, String name, MedicineGroup group,
                                             List<Version> versions) {
        return new Medicine(id, name, "Farmak", group,
                List.of("Ospamox", "Flemoxin Solutab"), versions);
    }

    @Test
    void handlerBuildsMedicineWhenTextArrivesInChunksWithWhitespace() {
        start("Medicines");
        startMedicine("MED-001", "Amoxicillin", "antibiotic");
        version("capsule", "Farmak");
        endMedicine();
        end("Medicines");

        assertEquals(List.of(expectedMedicine("MED-001", "Amoxicillin",
                        MedicineGroup.ANTIBIOTIC, List.of(TestData.version(MedicineForm.CAPSULE)))),
                handler.getMedicines());
    }

    @Test
    void handlerKeepsDocumentOrderForSeveralMedicinesVersionsAndManufacturers() {
        start("Medicines");
        startMedicine("MED-001", "Amoxicillin", "antibiotic");
        version("capsule", "Farmak");
        endMedicine();
        startMedicine("MED-002", "Ibuprofen", "analgesic");
        version("capsule", "Farmak", "Darnitsa");
        version("tablet", "Farmak");
        endMedicine();
        end("Medicines");

        Version capsule = new Version(MedicineForm.CAPSULE,
                List.of(TestData.manufacturer("Farmak"), TestData.manufacturer("Darnitsa")));
        Version tablet = TestData.version(MedicineForm.TABLET);
        List<Medicine> medicines = handler.getMedicines();

        assertEquals(2, medicines.size());
        assertEquals(expectedMedicine("MED-001", "Amoxicillin", MedicineGroup.ANTIBIOTIC,
                List.of(TestData.version(MedicineForm.CAPSULE))), medicines.get(0));
        assertEquals(expectedMedicine("MED-002", "Ibuprofen", MedicineGroup.ANALGESIC,
                List.of(capsule, tablet)), medicines.get(1));
    }

    @Test
    void handlerReturnsNoMedicinesForEmptyDocument() {
        start("Medicines");
        end("Medicines");

        assertTrue(handler.getMedicines().isEmpty());
    }

    @Test
    void getMedicinesReturnsUnmodifiableSnapshot() {
        startMedicine("MED-001", "Amoxicillin", "antibiotic");
        version("capsule", "Farmak");
        endMedicine();

        List<Medicine> snapshot = handler.getMedicines();
        startMedicine("MED-002", "Ibuprofen", "analgesic");
        version("tablet", "Farmak");
        endMedicine();

        assertEquals(1, snapshot.size());
        assertEquals(2, handler.getMedicines().size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(snapshot.get(0)));
    }

    @Test
    void handlerReportsInvalidMedicineWhenItsElementEnds() {
        startMedicine("MED-001", "Amoxicillin", "magic");
        version("capsule", "Farmak");

        MedicineParseException exception = assertThrows(MedicineParseException.class,
                this::endMedicine);

        assertEquals("Invalid medicine: Unknown MedicineGroup value: magic",
                exception.getMessage());
    }

    @Test
    void handlerReportsInvalidManufacturerWhenItsElementEnds() {
        startMedicine("MED-001", "Amoxicillin", "antibiotic");
        start("Version", "form", "capsule");

        MedicineParseException exception = assertThrows(MedicineParseException.class,
                () -> manufacturer("Farmak", "jar"));

        assertEquals("Invalid manufacturer: Unknown PackageKind value: jar",
                exception.getMessage());
    }
}