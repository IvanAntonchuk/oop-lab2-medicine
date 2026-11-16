package ua.antonchuk.medicine.parser;

import ua.antonchuk.medicine.model.Manufacturer;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.Version;
import ua.antonchuk.medicine.util.EnumParser;

import java.util.ArrayList;
import java.util.List;

public class VersionBuilder extends ModelBuilder<Version> {

    private String form;
    private final List<Manufacturer> manufacturers = new ArrayList<>();

    public VersionBuilder form(String form) {
        this.form = form;
        return this;
    }

    public VersionBuilder addManufacturer(Manufacturer manufacturer) {
        manufacturers.add(manufacturer);
        return this;
    }

    @Override
    protected Version create() {
        return new Version(EnumParser.parse(MedicineForm.class, form), manufacturers);
    }

    @Override
    protected String description() {
        return "version";
    }
}