package ua.antonchuk.medicine.model;

import ua.antonchuk.medicine.util.Validation;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class Version {

    private final MedicineForm form;
    private final List<Manufacturer> manufacturers;

    public Version(MedicineForm form, List<Manufacturer> manufacturers) {
        this.form = Validation.requireNonNull(form, "Medicine form");
        this.manufacturers = Validation.requireNonEmptyList(manufacturers, "Manufacturers");
    }

    public MedicineForm getForm() {
        return form;
    }

    public List<Manufacturer> getManufacturers() {
        return manufacturers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version that)) {
            return false;
        }
        return form == that.form && manufacturers.equals(that.manufacturers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(form, manufacturers);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Version [form=%s, manufacturers=%s]", form, manufacturers);
    }
}