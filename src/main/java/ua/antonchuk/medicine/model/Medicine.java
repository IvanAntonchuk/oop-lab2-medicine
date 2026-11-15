package ua.antonchuk.medicine.model;

import ua.antonchuk.medicine.util.Validation;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class Medicine {

    private final String id;
    private final String name;
    private final String pharm;
    private final MedicineGroup group;
    private final List<String> analogs;
    private final List<Version> versions;

    public Medicine(String id, String name, String pharm, MedicineGroup group,
                    List<String> analogs, List<Version> versions) {
        this.id = Validation.requireText(id, "Id");
        this.name = Validation.requireText(name, "Name");
        this.pharm = Validation.requireText(pharm, "Pharm");
        this.group = Validation.requireNonNull(group, "Group");
        this.analogs = Validation.copyOfList(analogs, "Analogs");
        this.versions = Validation.requireNonEmptyList(versions, "Versions");
        requireUniqueForms(this.versions);
    }

    private static void requireUniqueForms(List<Version> versions) {
        Set<MedicineForm> forms = EnumSet.noneOf(MedicineForm.class);
        for (Version version : versions) {
            if (!forms.add(version.getForm())) {
                throw new IllegalArgumentException(
                        "Duplicate medicine form: " + version.getForm());
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPharm() {
        return pharm;
    }

    public MedicineGroup getGroup() {
        return group;
    }

    public List<String> getAnalogs() {
        return analogs;
    }

    public List<Version> getVersions() {
        return versions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Medicine that)) {
            return false;
        }
        return id.equals(that.id)
                && name.equals(that.name)
                && pharm.equals(that.pharm)
                && group == that.group
                && analogs.equals(that.analogs)
                && versions.equals(that.versions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pharm, group, analogs, versions);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Medicine [id=%s, name=%s, pharm=%s, group=%s, analogs=%s, versions=%s]",
                id, name, pharm, group, analogs, versions);
    }
}