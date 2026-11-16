package ua.antonchuk.medicine.parser;

import ua.antonchuk.medicine.model.Medicine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MedicineEventHandler {

    private final List<Medicine> medicines = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private MedicineBuilder medicine;
    private VersionBuilder version;
    private ManufacturerBuilder manufacturer;

    public void startElement(String name, Function<String, String> attribute) {
        text.setLength(0);
        switch (name) {
            case XmlNames.MEDICINE ->
                    medicine = new MedicineBuilder().id(attribute.apply(XmlNames.ID));
            case XmlNames.VERSION ->
                    version = new VersionBuilder().form(attribute.apply(XmlNames.FORM));
            case XmlNames.MANUFACTURER -> manufacturer = new ManufacturerBuilder()
                    .name(attribute.apply(XmlNames.MANUFACTURER_NAME));
            case XmlNames.CERTIFICATE -> manufacturer.certificate(
                    attribute.apply(XmlNames.NUMBER),
                    attribute.apply(XmlNames.ISSUED),
                    attribute.apply(XmlNames.EXPIRES));
            case XmlNames.PACKAGE -> manufacturer.medicinePackage(
                    attribute.apply(XmlNames.TYPE),
                    attribute.apply(XmlNames.QUANTITY),
                    attribute.apply(XmlNames.PRICE));
            case XmlNames.DOSAGE -> manufacturer.dosage(
                    attribute.apply(XmlNames.UNIT),
                    attribute.apply(XmlNames.AMOUNT),
                    attribute.apply(XmlNames.TIMES_PER_DAY));
            default -> {
            }
        }
    }

    public void characters(String chars) {
        text.append(chars);
    }

    public void endElement(String name) {
        String value = text.toString().trim();
        switch (name) {
            case XmlNames.NAME -> medicine.name(value);
            case XmlNames.PHARM -> medicine.pharm(value);
            case XmlNames.GROUP -> medicine.group(value);
            case XmlNames.ANALOG -> medicine.addAnalog(value);
            case XmlNames.ORGANIZATION -> manufacturer.organization(value);
            case XmlNames.MANUFACTURER -> version.addManufacturer(manufacturer.build());
            case XmlNames.VERSION -> medicine.addVersion(version.build());
            case XmlNames.MEDICINE -> medicines.add(medicine.build());
            default -> {
            }
        }
    }

    public List<Medicine> getMedicines() {
        return List.copyOf(medicines);
    }
}