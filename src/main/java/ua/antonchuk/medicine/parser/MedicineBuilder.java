package ua.antonchuk.medicine.parser;

import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.model.MedicineGroup;
import ua.antonchuk.medicine.model.Version;
import ua.antonchuk.medicine.util.EnumParser;

import java.util.ArrayList;
import java.util.List;

public class MedicineBuilder extends ModelBuilder<Medicine> {

    private String id;
    private String name;
    private String pharm;
    private String group;
    private final List<String> analogs = new ArrayList<>();
    private final List<Version> versions = new ArrayList<>();

    public MedicineBuilder id(String id) {
        this.id = id;
        return this;
    }

    public MedicineBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MedicineBuilder pharm(String pharm) {
        this.pharm = pharm;
        return this;
    }

    public MedicineBuilder group(String group) {
        this.group = group;
        return this;
    }

    public MedicineBuilder addAnalog(String analog) {
        analogs.add(analog);
        return this;
    }

    public MedicineBuilder addVersion(Version version) {
        versions.add(version);
        return this;
    }

    @Override
    protected Medicine create() {
        return new Medicine(id, name, pharm, EnumParser.parse(MedicineGroup.class, group),
                analogs, versions);
    }

    @Override
    protected String description() {
        return "medicine";
    }
}